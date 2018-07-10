append([],Z,Z).
append([X|Xs],L2,[X|Z]):-append(Xs,L2,Z).

prefix(L1,L2):-append(L1,Z,L2).