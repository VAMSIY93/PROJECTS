let rec mem_tuple n l3=match l3 with
		 []->false
		|(x,y)::ls->if x=n then true else (mem_tuple n ls)
;;

let rec check_sig l=match l with
		 []->true
		|(x,y)::ls->if (mem_tuple x ls) then false else if y<0 then false else (check_sig ls)
;;

let rec member n l = match l with
		 []->false
		|x::xs->if n=x then true else (member n xs)
;;

type variable = string;;

type symbol = string;;

type term = V of variable | Node of symbol * (term list);;

let negate x = match x with
		|true->false
		|false->true
;;

let rec calc_truth l = match l with
		 []->true
		|x::xs->x && (calc_truth xs)
;; 

let rec mem_tuple_arity n l3 c=match l3 with
		 []->false
		|(x,y)::ls->if (x=n)&&(y=c) then true else (mem_tuple_arity n ls c)
;;

let rec wfterm l1 term1 = match term1 with
		 V s->true
		|Node(x,l2)->if negate(mem_tuple_arity x l1 (List.length l2)) then false else (calc_truth (List.map (wfterm l1) l2))
;;

let signature = [("*",2);("+",2);("+",1);("a",0)];;

wfterm signature (Node("*",[Node("-",[V "a";V "b"]);V "c"]));;

let rec calc_max l = match l with
		 []->0
		|x::xs->max x (calc_max xs)
;;

let rec ht term = match term with
		 V s->1
		|Node(x,l1)->1 + calc_max (List.map ht l1)
;;

let rec calc_sum l = match l with
		 []->0
		|x::xs->x + calc_sum xs
;;

let rec size term = match term with
		 V s->1
		|Node(x,l1)->calc_sum (List.map size l1)
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

(* The substitution is represented as ( (varibale * term) list) and composition of substitution is represented as List of substitutions ,ie (variable * term) list list  *)

let sub = [(V "P",Node("#",[V "M";V "N"]));(V "Q",Node("*",[]))];;

let rec substitute z sub = match sub with
			 []->z
			|((x,y)::xs)->if x=z then y else substitute z xs
;;

let rec subst sub term = match term with
		 V s->(substitute (V s) sub)
		|Node(x,l)->Node(x,(List.map (subst sub) l))
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
		 (V s1,V s2)->if s1=s2 then list else if ((is_in_list (V s1) list)&&(is_in_list (V s2) list)&&((return_subst (V s1) list)=(return_subst (V s2) list))) then list else if ((is_in_list (V s1) list)&&(is_in_list (V s2) list)&&negate((return_subst (V s1) list)=(return_subst (V s2) list))) then (raise NON_UNIFIABLE) else if ((is_in_list (V s1) list)&&negate(is_in_list (V s2) list)&&negate(is_in_term (V s2) (return_subst (V s1) list))) then (V s2,(return_subst (V s1) list))::list else if ((is_in_list (V s1) list)&&negate(is_in_list (V s2) list)&&(is_in_term (V s2) (return_subst (V s1) list))) then (raise NON_UNIFIABLE) else if (negate(is_in_list (V s1) list)&&(is_in_list (V s2) list)&&negate(is_in_term (V s1) (return_subst (V s2) list))) then (V s1,(return_subst (V s2) list))::list else if (negate(is_in_list (V s1) list)&&(is_in_list (V s2) list)&&(is_in_term (V s1) (return_subst (V s2) list))) then (raise NON_UNIFIABLE) else (V s1,V s2)::list
		|(V s,Node(x,[]))->if (negate(is_in_list (V s) list)) then (V s,Node(x,[]))::list else (raise NON_UNIFIABLE)
		|(V s,Node(x,l))->if (negate(is_in_term (V s) (Node(x,l)))&&negate(is_in_list (V s) list)) then (V s,Node(x,l))::list else (raise NON_UNIFIABLE)
		|(Node(x1,[]),Node(x2,[]))->if (x1=x2) then list else (raise NON_UNIFIABLE)
		|(Node(x1,[]),Node(x2,l))->raise NON_UNIFIABLE
		|(Node(x,l),V s)->if (negate(is_in_term (V s) (Node(x,l)))&&negate(is_in_list (V s) list)) then (V s,Node(x,l))::list else (raise NON_UNIFIABLE)
		|(Node(x1,l1),Node(x2,l2))->if (x1=x2)&&((List.length l1)=(List.length l2)) then (intermediate (List.map2 (mgu_helper list) l1 l2))@list else (raise NON_UNIFIABLE)
;;

let mgu term1 term2 = mgu_helper [] term1 term2;;
