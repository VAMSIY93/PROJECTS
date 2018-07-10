member(X,[]) :- false.
member(X,[X|Xs]).
member(X,[Y|Xs]) :- member(X,Xs).

union([],L,L).
union([X|Xs],L2,L3):-member(X,L2),union(Xs,L2,L3).
union([X|Xs],L2,L3):-not(member(X,L2)),union(Xs,[X|L2],L3).


intersection()