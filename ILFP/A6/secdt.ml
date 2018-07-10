let given_1 = [App(App(Abs("x", [Abs("y", [Add(Var "x" , Var "y")])]), Int 2), Int 3)];;
let given_2 = [App(Abs("x", [App(Var "x", Int 2)]), Abs("y", [Add( Var "y", Int 3)]))];;

let given_3 = [If_then_else(Equal(Int 2, Int 2),Int 4,Div(Int 1, Int 0))];;

let my1 = [Equal(Int 3, Int 2)];;
let my2 = [Not(Boolean true)];;

let my3 = [And(Boolean true, Boolean false)];;
																			
let my4 = [First(Pair(Int 5,Add(Int 5,Int 6)))];;
let my5 = [App(App(Abs("x", [Abs("y", [Add(Var "x" , Var "y")])]), Int 2), App(Abs("z", [Add(Int 9 , Var "z")]), Int 5))];;

let my6 = [App(App(Abs("x", [Abs("y", [If_then_else(More(Add(Var "x" , Var "y"),Mul(Var "x" , Var "y")),App(Abs("z", [Add(Int 9 , Var "z")]), Int 5),Div(Int 1, Int 0))])]), Int 2), Int 10)];;




let my7 = App(Abs("x", [Abs("m", [Add(Var "x" , Var "y")])]), Int 3);;

let prog = [Mul(Int 1 , Int 2);Add(Int 6, Int 10)];;
(*let cll = [App(Abs("x",newi),Int 50); Add(Int 1 , Int 2); Mul(Int 2, Int 6)];;*)

let div = [Div(Int 1,Int 0)];;

let b = [If_then_else(Equal(Int 3,Int 3),Int 4,div)];;
