male(shantanu).
male(parashar).
male(bhishma).
male(chitrangada).
male(vichitravirya).
male(vyasa).
male(dhritarashtra).
male(pandu).
male(kauravas).
male(yuyutsu).
male(yudhisthira).
male(bhima).
male(arjuna).
male(nakula).
male(sahadeva).
male(abhimanyu).
male(parikshita).
male(janamejaya).
male(parativindhya).
male(sutasoma).
male(srutakirti).
male(satanika).
male(srukarma).
female(ganga).
female(satyavati).
female(ambika).
female(ambalika).
female(gandhari).
female(vaishya).
female(kunti).
female(madri).
female(draupadi).
female(subhadra).
female(uttara).
female(madravati).
child(bhishma, ganga).
child(bhishma, shantanu).
child(vyasa, parashar).
child(vyasa, satyavati).
child(chitrangada, shantanu).
child(chitrangada, satyavati).
child(vichitravirya, shantanu).
child(vichitravirya, satyavati).
child(dhritarashtra, ambalika).
child(pandu, ambalika).
child(pandu, vichitravirya).
child(dhritarashtra, vichitravirya).
child(kauravas, gandhari).
child(kauravas, dhritarashtra).
child(yuyutsu, vaishya).
child(yuyutsu, dhritarashtra).
child(yudhisthira, kunti).
child(yudhisthira, pandu).
child(bhima, kunti).
child(bhima, pandu).
child(arjuna, kunti).
child(arjuna, pandu).
child(nakula, madri).
child(nakula, pandu).
child(sahadeva, madri).
child(sahadeva, pandu).
child(abhimanyu, subhadra).
child(abhimanyu, arjuna).
child(parikshita, abhimanyu).
child(parikshita, uttara).
child(janamejaya, parikshita).
child(janamejaya, madravati).
child(parativindhya, draupadi).
child(parativindhya, yudhisthira).
child(sutasoma, bhima).
child(sutasoma, draupadi).
child(srukarma, draupadi).
child(srukarma, arjuna).
child(satanika, draupadi).
child(satanika, nakula).
child(srutakirti, draupadi).
child(srutakirti, sahadeva).
married(ganga, shantanu).
married(satyavati, shantanu).
married(gandhari, dhritarashtra).
married(vaishya, dhritarashtra).
married(kunti, pandu).
married(madri, pandu).
married(draupadi, yudhisthira).
married(draupadi, bhima).
married(draupadi, arjuna).
married(draupadi, nakula).
married(draupadi, sahadeva).
married(subhadra, arjuna).
married(uttara, abhimanyu).
married(madravati, parikshita).
married(shantanu, ganga).
married(shantanu, satyavati).
married(dhritarashtra, gandhari).
married(dhritarashtra, vaishya).
married(pandu, kunti).
married(pandu, madri).
married(yudhisthira, draupadi).
married(bhima, draupadi).
married(arjuna, draupadi).
married(nakula, draupadi).
married(sahadeva, draupadi).
married(arjuna, subhadra).
married(abhimanyu, uttara).
married(parikshita, madravati).


parent(X,Y):-child(Y,X).
grandparent(Y,X):-child(X,Z),child(Z,Y).
son(X,Y):-male(X),child(X,Y).
daughter(X,Y):-female(X),child(X,Y).
father(X,Y):-male(X),child(Y,X).
mother(X,Y):-female(X),child(Y,X).
sibbling(X,Y):-child(X,Z),child(Y,Z),father(Z,X),father(Z,Y),X\=Y.
brother(X,Y):-male(X),sibbling(X,Y).
sister(X,Y):-female(X),sibbling(X,Y).
grandmother(X,Y):-female(X),child(Z,X),child(Y,Z).
grandfather(X,Y):-male(X),child(Z,X),child(Y,Z).
husband(X,Y):-male(X),female(Y),married(X,Y).
wife(X,Y):-female(X),male(Y),married(X,Y).
grandson(X,Y):-male(X),grandparent(Y,X).
granddaughter(X,Y):-female(X),grandparent(Y,X).
greatgrandfather(X,Y):-male(X),child(Z,X),child(W,Z),child(Y,W).
greatgrandmother(X,Y):-female(X),child(Z,X),child(W,Z),child(Y,W).
father-in-law(X,Y):-married(Y,Z),father(X,Z).
mother-in-law(X,Y):-married(Y,Z),mother(X,Z).
son-in-law(X,Y):-male(X),child(Z,Y),married(Z,X).
daughter-in-law(X,Y):-female(X),child(Z,Y),married(Z,X).
brother-in-law(X,Y):-male(X),married(X,Z),sibbling(Z,Y).
sister-in-law(X,Y):-female(X),married(X,Z),sibbling(Z,Y).
uncle(X,Y):-male(X),parent(Z,Y),brother(X,Z);male(X),parent(Z,Y),brother-in-law(X,Z).
aunt(X,Y):-female(X),parent(Z,Y),sister(X,Z);female(X),parent(Z,Y),sister-in-law(X,Z).
cousin(X,Y):-child(X,P),sibbling(P,Z),child(Y,Z).
nephew(X,Y):-male(X),sibbling(Z,X),child(Y,Z).
niece(X,Y):-female(X),sibbling(Z,X),child(Y,Z).