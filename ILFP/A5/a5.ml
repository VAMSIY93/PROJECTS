(*#use "assign3.ml";;
*)
type variable = string;;

type symbol = string;;

type term = V of variable | Node of symbol * (term list);;

type formula = P of symbol * (term list);;

type clause = F of term | R of term * (term list);;

type program = clause list;;

let negate x = match x with
		|true->false
		|false->true
;;

let rec member n l = match l with
		 []->false
		|x::xs->if n=x then true else (member n xs)
;;

let rec calc_union l1 = match l1 with
		 []->[]
		|x::xs->if (member x xs) then xs else x::(calc_union xs)
;;

let rec intermediate l = match l with
		 []->[]
		|x::xs->x@(intermediate xs)
;;

let rec vars term = match term with
		 V s->[V s]
		|Node(x,l1)->calc_union (intermediate (List.map vars l1))
;;

exception NON_UNIFIABLE;;

let rec is_in_term x term = member x (vars term);;

let rec is_in_list s1 list = match list with
			 []->false
			|(x,y)::xs->if x=s1 then true else (is_in_list s1 xs)
;;

let rec return_subst s1 list = match list with
			 []->raise NON_UNIFIABLE
			|(x,y)::xs->if x=s1 then y else return_subst s1 xs
;;

let rec mgu_helper list term1 term2 = match (term1,term2) with		
		 (V s1,V s2)->if s1=s2 then list else if ((is_in_list (V s1) list)&&(is_in_list (V s2) list)&&((return_subst (V s1) list)=(return_subst (V s2) list))) then list else if ((is_in_list (V s1) list)&&(is_in_list (V s2) list)&&negate((return_subst (V s1) list)=(return_subst (V s2) list))) then [] else if ((is_in_list (V s1) list)&&negate(is_in_list (V s2) list)&&negate(is_in_term (V s2) (return_subst (V s1) list))) then (V s2,(return_subst (V s1) list))::list else if ((is_in_list (V s1) list)&&negate(is_in_list (V s2) list)&&(is_in_term (V s2) (return_subst (V s1) list))) then [] else if (negate(is_in_list (V s1) list)&&(is_in_list (V s2) list)&&negate(is_in_term (V s1) (return_subst (V s2) list))) then (V s1,(return_subst (V s2) list))::list else if (negate(is_in_list (V s1) list)&&(is_in_list (V s2) list)&&(is_in_term (V s1) (return_subst (V s2) list))) then [] else (V s1,V s2)::list
		|(V s,Node(x,[]))->if (negate(is_in_list (V s) list)) then (V s,Node(x,[]))::list else []
		|(V s,Node(x,l))->if (negate(is_in_term (V s) (Node(x,l)))&&negate(is_in_list (V s) list)) then (V s,Node(x,l))::list else []
		|(Node(x1,[]),Node(x2,[]))->if (x1=x2) then list else []
		|(Node(x1,[]),Node(x2,l))->[]
		|(Node(x,l),V s)->if (negate(is_in_term (V s) (Node(x,l)))&&negate(is_in_list (V s) list)) then (V s,Node(x,l))::list else []
		|(Node(x1,l1),Node(x2,l2))->if (x1=x2)&&((List.length l1)=(List.length l2)) then (intermediate (List.map2 (mgu_helper list) l1 l2))@list else []
;;

let mgu term1 term2 = mgu_helper [] term1 term2;;

let rec executRule unifiers fact_list prog fin_terms output = match prog with
															 []->output
															|x::xs->(match (x,fact_list) with
																	 (x,[])->output
																	|(x,y::ys)->




let rec helpQuery prog goal output = match prog with
									 []->output
									|x::xs->(match (x,goal) with
											 (F(pred),goal)->(helpQuery xs goal (output@(mgu pred goal)) )
											|_->(helpQuery xs goal output))											
;;


let runQuery prog goal = helpQuery prog goal [];;

							

let fact1 = F(Node("child",[Node("arjun",[]);Node("bhima",[])]));;
let fact2 = F(Node("child",[Node("surya",[]);Node("krishna",[])]));;
let fact3 = F(Node("child",[Node("harsha",[]);Node("jaggu",[])]));;
let fact4 = F(Node("child",[Node("rama",[]);Node("ravana",[])]));;
let fact5 = F(Node("child",[Node("god",[]);Node("God",[])]));;

let prog = [fact1;fact2;fact3;fact4;fact5];;


let rec helperHelp elem unifiers = match (elem,unifiers) with
								 (elem,[])->[]
								|(elem,((x,y)::xs))->if elem=x then [(x,y)]@(helperHelp elem xs) else (helperHelp elem xs)
;;

let rec helpFUN unifiers term_list = match term_list with
									 []->[]
									|(x::xs)->(helperHelp x unifiers)@(helpFUN unifiers xs)
;; 

let term_list = [V "X";V "Y"];;

let unifiers = [(V "X",Node("abc",[]));(V "Z",Node("fabc",[]));(V "Y",Node("abec",[]));(V "W",Node("abcd",[]))];;