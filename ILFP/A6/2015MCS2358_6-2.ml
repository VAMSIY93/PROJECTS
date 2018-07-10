type term = Var of string 
		| Int of int 
		| Bool of bool 
		| Abs of string* term list
		| Add of term*term 
		| Mul of term*term 
		| Div of term * term
		| App of term*term
		| Equal of term*term 
		| Not of term 
		| And of term*term 
		| Or of term*term
		| If_then_else of term*term*term
;;

type closure = Closure of term* table
and table = Table of (string * closure) list;;

let add t1 t2 = match t1,t2 with 
Int(a) , Int(b) -> Int(a+b) ;;


let multiply t1 t2 = match t1,t2 with 
Int(a) , Int(b) -> Int(a*b) ;;

let division t1 t2 = match t1,t2 with
Int(a) , Int(b) -> Int(a/b) ;;

let rec unload x etable = match etable with
Table((v,cl)::l)-> if v= x then cl else (unload x (Table l));;

let rec execute_prog instr st = match instr with
Closure(Int i,etable) -> Int(i)
|Closure(Bool i,etable) -> Bool(i)
|Closure(App(e1,e2),etable) -> execute_prog (Closure(e1,etable)) (Closure(e2,etable)::st)
|Closure(Abs(s,li),Table t) -> execute_prog (Closure((List.hd li) ,(Table ((s,(List.hd st))::t)))) (List.tl st)
|Closure(Var x ,etable) ->  execute_prog ((unload x etable)) st
|Closure(Add(a,b),etable)-> add  (execute_prog (Closure(a,etable)) []) (execute_prog (Closure(b,etable)) [])
|Closure(Mul(a,b),etable)->  multiply (execute_prog (Closure(a,etable)) []) (execute_prog (Closure(b,etable)) [])
|Closure(Div(a,b),etable)->  division (execute_prog (Closure(a,etable)) []) (execute_prog (Closure(b,etable)) [])
|Closure(Equal(a,b),etable)-> if  (execute_prog (Closure(a,etable)) []) = (execute_prog (Closure(b,etable)) []) then Bool(true) else Bool(false)
|Closure(Not(a),etable)->if(execute_prog (Closure(a,etable)) []) = Bool(true) then Bool(false) else Bool(true)
|Closure(And(a,b),etable)-> if  ((execute_prog (Closure(a,etable)) []) = Bool(true) )&& ((execute_prog (Closure(b,etable)) []) = Bool(true)) then Bool(true) else Bool(false)
|Closure(Or(a,b),etable)-> if  ((execute_prog (Closure(a,etable)) []) = Bool(true) )|| ((execute_prog (Closure(b,etable)) []) = Bool(true)) then Bool(true) else Bool(false)
|Closure(If_then_else(e1,e2,e3),etable)-> if ((execute_prog (Closure(e1,etable)) []) = Bool(true) ) then (execute_prog (Closure(e2,etable)) []) else (execute_prog (Closure(e3,etable)) [])
;;


let krivine arg = execute_prog (Closure(arg,Table ([]))) [];;


let x = Mul((Add((Add(Int 1 , Int 2)),(Mul(Int 1 , Int 2)))),Int 6);;

let y = Add(Int 1, Int 2);;

let z = App(App(Abs("X", [Abs("Y", [Add(Var "X",Var "Y")])]),Int 2),Int 3);;

let w = App(Abs("F",[App(Var "F",Int 2)]),Abs("Y",[Add(Var "Y",Int 3)]));;

let prog_app = Add(Int 10,App(Abs("X",[Add(Var "X",Int 2)]),Int 3));;

let a = App(Abs("F",[App(Var "F",Int 2)]),Abs("Y",[Add(Var "Y",Int 3)]));;

if 3 > 2 then 4 else (1/0)

let b = If_then_else(Equal(Int 3,Int 2),Int 1,Int 0);;