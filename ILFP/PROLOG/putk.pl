putk(X,1,L,[X|L]).
putk(X,K,[F|L],[F|LX]):-K1 is K-1,putk(X,K1,L,LX).