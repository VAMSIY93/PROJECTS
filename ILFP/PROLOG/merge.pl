merge([],L,L).
merge(L,[],L).
merge([X|Xs],[Y|Ys],[X,Y|L3]):-merge(Xs,Ys,L3).