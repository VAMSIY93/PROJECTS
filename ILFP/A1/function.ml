(** issubset takes range from 1-100 **)

issubset f_s1 f_s2 100;;

ismember 5 f_s1;;
ismember 15 f_s2;;

union f_s1 f_s2 14;;
union f_s1 f_s2 30;;

isequal f_s1 f_s1 100;;
isequal f_s1 f_s2 100;;

(** isempty takes range from 1-100 **)

isempty f_s1 100;;
isempty f_s1 2;;

intersection f_s1 f_s2 15;;
intersection f_s1 f_s2 5;;

difference f_s1 f_s2 9;;
difference f_s1 f_s2 15;;




