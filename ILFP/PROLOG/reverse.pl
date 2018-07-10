rev([],Z,Z).
rev([X|Xs],Z,Y):-rev(Xs,[X|Z],Y).