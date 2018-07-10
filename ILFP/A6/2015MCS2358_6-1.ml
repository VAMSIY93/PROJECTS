type term = Var of string
		|Int of int	
		|Bool of bool 
		|Unit of unit
		|Equal of term* term
		|Add of term* term
		|Mul of term* term
		|Div of term* term
		|And of term* term
		|Or of term* term
		|Not of term
		|First of term
		|Second of term
		|Less of term* term
		|More of term* term
		|If_then_else of term* term* term
		|Abs of string* term list
		|App of term* term
		|Tuple of term* term 
;;

type oppcode = ADD 
			|MUL 
			|DIV
			|AND 
			|ORR
			|NOT 
			|EQUALS
			|LESS 
			|MORE 
			|COND
			|CALL
			|RETURN
			|FIRST
			|SECOND
			|TUPLECODE
			|TUPLE of oppcode* oppcode
			|CONST of int
			|LOOKUP of string
			|BOOLEAN of bool
			|CLOSURE of string* oppcode list
;;


type environment = (string * oppcode) list;;

type element = E of oppcode
			|Closure of (string * oppcode list * environment)
;;

type stack = element list;;

type dump = (stack * oppcode list * environment) list;;

(* App(App(Abs(x,Abs(y,x+y)),2),3) *)
(* App(App(Abs(Var "X",Abs(Var "Y",Var "X"+Var "Y")),2),3) *)

exception NOT_PRESENT;;

exception NON_APPLICABLE;;


let rec compile_abs prog_l = match prog_l with
						 []->[]
						|x::xs->(compile x)@(compile_abs xs)
						
	and compile inst = match inst with 
					 Var v->[LOOKUP(v)]
					|Int i->[CONST(i)]
					|Bool b->[BOOLEAN(b)]
					|Add(e1,e2)->(compile e1)@(compile e2)@[ADD]
					|Mul(e1,e2)->(compile e1)@(compile e2)@[MUL]
					|Div(e1,e2)->(compile e1)@(compile e2)@[DIV]
					|And(e1,e2)->(compile e1)@(compile e2)@[AND]
					|Or(e1,e2)->(compile e1)@(compile e2)@[ORR]
					|Not(e1)->(compile e1)@[NOT]
					|Equal(e1,e2)->(compile e1)@(compile e2)@[EQUALS]
					|Less(e1,e2)->(compile e1)@(compile e2)@[LESS]
					|More(e1,e2)->(compile e1)@(compile e2)@[MORE]
					|First(x)->(compile x)@[FIRST]
					|Second(x)->(compile x)@[SECOND]
					|If_then_else(e1,e2,e3)->(compile e1)@(compile e2)@(compile e3)@[COND]
					|App(e1,e2)->(compile e1)@(compile e2)@[CALL]			
					|Abs(e1,e2)->[CLOSURE(e1,((compile_abs e2)@[RETURN]))]
					|Tuple(x,y)->(compile x)@(compile y)@[TUPLECODE]
					|_->[]		
;;  


let rec fetch elem environ = match environ with 
								 []->raise NOT_PRESENT
								|x::xs->match x with
										(y,t)-> if y=elem then t else (fetch elem xs) 
;;

let rec sub ol environ = match environ with
							 []					->	ol
							|x::xs-> sub (help_sub ol x) xs

and help_sub ol binding  = match (ol,binding) with
								|([],_)->[]
								|((y::ys),(v,bind))-> match y with
															 LOOKUP(v1)->if(v1=v) then bind::(help_sub ys binding)
																				else y::(help_sub ys binding)
															|CLOSURE(v2,newlist)-> if(v=v2) then y::(help_sub ys binding)
																				else CLOSURE(v2,(sub newlist [binding]))::(help_sub ys binding)
															|_ -> y::(help_sub ys binding)
;;


let rec execute_prog stack env code dump = match code with
												 []->(match dump with
												 	 []->(match (List.hd stack) with
												 		|E(v)->v)
												 	|((se,ol,environ)::tail)-> (execute_prog se environ ol tail))
												    
												|(x::xs) -> (match x with
																 LOOKUP(x)->(match (fetch x env) with
																					CLOSURE(x,newoplist1) -> execute_prog (Closure(x,newoplist1,[])::stack) env xs dump
																					|_-> execute_prog (E(fetch x env)::stack) env xs dump)
																|CONST(i)-> (execute_prog (E(x)::stack) env xs dump)
																|BOOLEAN(b)-> (execute_prog (E(x)::stack) env xs dump)
																|ADD-> (match stack with 
																			(E(CONST(first))::tail)-> (match tail with
																									 (E(CONST(second))::rem)-> (execute_prog (E(CONST(first+second))::rem) env xs dump)) )
																|MUL-> (match stack with 
																			(E(CONST(first))::tail)-> (match tail with
																									 (E(CONST(second))::rem)-> (execute_prog (E(CONST(first*second))::rem) env xs dump)) )
																|DIV-> (match stack with 
																			(E(CONST(first))::tail)-> (match tail with
																									 (E(CONST(second))::rem)-> (execute_prog (E(CONST(second/first))::rem) env xs dump)) )
																|AND-> (match stack with 
																			(E(BOOLEAN(first))::tail)-> (match tail with
																									 (E(BOOLEAN(second))::rem)-> (execute_prog (E(BOOLEAN(first&&second))::rem) env xs dump)) )
																|ORR-> (match stack with 
																			(E(BOOLEAN(first))::tail)-> (match tail with
																									 (E(BOOLEAN(second))::rem)-> (execute_prog (E(BOOLEAN(first || second))::rem) env xs dump)) )
																|NOT-> (match stack with 
																			(E(BOOLEAN(first))::tail)-> execute_prog (E(BOOLEAN(not(first)))::tail) env xs dump)
																|EQUALS-> (match stack with
																				(E(CONST(first))::tail)	->( match tail with
																										| (E(CONST(second))::rem)-> if(first=second) then execute_prog (E(BOOLEAN(true))::rem) env xs dump
																																							else execute_prog (E(BOOLEAN(false))::rem) env xs dump))
																|LESS-> (match stack with
																				(E(CONST(first))::tail)	->( match tail with
																										| (E(CONST(second))::rem)-> if(first>second) then execute_prog (E(BOOLEAN(true))::rem) env xs dump
																																							else execute_prog (E(BOOLEAN(false))::rem) env xs dump))
																|MORE-> (match stack with
																				(E(CONST(first))::tail)	->( match tail with
																										| (E(CONST(second))::rem)-> if(first<second) then execute_prog (E(BOOLEAN(true))::rem) env xs dump
																																							else execute_prog (E(BOOLEAN(false))::rem) env xs dump))																
																|COND-> ( match stack with
																				| 	(E(CONST(first))::tail)	-> 	( match tail with
																										| 	(E(CONST(second))::rem)-> (match rem with
																																		| 	(E(BOOLEAN(t_top))::t_tail)-> if (t_top) then (execute_prog (E(CONST(second))::t_tail) env xs dump) else (execute_prog (E(CONST(first))::t_tail) env xs dump))))
																|CLOSURE(v,slist)->(execute_prog (Closure(v,slist,env)::stack) env xs dump)
																|CALL->( match stack with	
																				| 	(E(BOOLEAN(val_clos))::tail)->( match tail with 
																										| 	(Closure(v,new_oplist,new_env)::rem)->(execute_prog [] ((v,BOOLEAN(val_clos))::new_env) new_oplist ((rem, xs, env)::dump)) )	
																				| 	(E(CONST(val_clos))::tail)->( match tail with 
																										| 	(Closure(v,new_oplist,new_env)::rem)->(execute_prog [] ((v,CONST(val_clos))::new_env) new_oplist ((rem,xs,env)::dump)) )	
																				| 	(Closure(v1,newoplist,newenv)::tail)-> (match tail with 
																										| 	(Closure(v2,oplist2,env2)::rem)-> (execute_prog [] ((v2,CLOSURE(v1,newoplist))::env2) oplist2 ((rem,xs,env)::dump))) )
																|RETURN-> (match dump with 
																				(old_stack,old_oplist,old_env)::rest_dump -> execute_prog ((List.hd stack)::old_stack) old_env old_oplist rest_dump	)
																|TUPLECODE-> (match stack with
																				(E(CONST(first))::tail) -> match tail with
																										(E(CONST(second))::rem) -> execute_prog (E(TUPLE(CONST(second),CONST(first)))::rem) env xs dump)
																|FIRST -> (match stack with
																					(E(TUPLE(x,y))::rem) -> execute_prog (E(x)::rem) env xs dump)
																|SECOND -> (match stack with
																					(E(TUPLE(x,y))::rem) -> execute_prog (E(y)::rem) env xs dump)


																 ) 
;;

let secd prog_list = execute_prog [] [] (compile_abs prog_list) [];;
(**)

let x = [Mul((Add((Add(Int 1 , Int 2)),(Mul(Int 1 , Int 2)))),Int 6)];;

let y = [Add(Int 1, Int 2)];;

let z = [App(App(Abs("X", [Abs("Y", [Add(Var "X",Var "Y")])]),Int 2),Int 3)];;

let w = [App(Abs("F",[App(Var "F",Int 2)]),Abs("Y",[Add(Var "Y",Int 3)]))];;

let prog_app = [Add(Int 10,App(Abs("X",[Add(Var "X",Int 2)]),Int 3))];;

let a = [App(Abs("F",[App(Var "F",Int 2)]),Abs("Y",[Add(Var "Y",Int 3)]))];;

if 3 > 2 then 4 else (1/0)

let b = [If_then_else(More(Int 3,Int 2),Int 1,Int 0)];;
