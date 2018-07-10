putk(X,1,L,[X|L]):-!.
putk(X,K,[F|L],[F|LX]):-putk(X,K1,L,LX),K1 is K-1.￼￼￼