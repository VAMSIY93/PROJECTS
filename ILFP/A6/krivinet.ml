let given_1 = App(App((Abs("x", [(Abs("y", [Add(Var "x" , Var "y")]))])), Int 2), Int 3);;

let given_2 = App((Abs("x", [App(Var "x", Int 2)])), (Abs("y", [Add( Var "y", Int 3)])));;


let my_1 = Equal(Int 3, Int 2);;

let my_2 = Not(Bool true);;

let my_3 = And(Bool true, Bool false);;

let my_5 = App(App((Abs("x", [(Abs("y", [Add(Var "x" , Var "y")]))])), Int 2), App((Abs("z", [Add(Int 9 , Var "z")])), Int 5));;

