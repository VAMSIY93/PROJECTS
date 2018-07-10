
%READ INPUT AND PREPROCESS DATA
choice = input('GIVE INPUT FORMAT   1. Alphabet(16)   2. HexaDigits(32) :  ');
pTxt = input('ENTER PLAIN TEXT:  ','s');
key = input('ENTER KEY:  ','s');

P = stringToMatrix(pTxt,choice);
K = stringToMatrix(key,choice);
K = K';

% GENERATE MULTIPLICATION AND INVERSE GF(2^8)
M = zeros(256);
for i=1:256
    for j=1:256
        M(i,j) = gmul(i-1,j-1);
    end
end
MInv = computeMulInverse(M);

%COMPUTE SBOXES
SBox = generateSBox(MInv);
InvSBox = generateInvSBox(MInv);

%EXPAND KEY
K = expandKey(K,SBox);

%AES ENCRYPTION ALGORITHM
I = addRoundKey(P,K(1:4,:)');

for i=1:10
    
    I = substituteBytes(I,SBox);
    I = shiftRows(I);
    if(i~=10)
        I = mixColumns(I);
    end
    ind = 4*i + 1;
    I = addRoundKey(I,K(ind:ind+3,:)');
end
C = I;

cTxt = matrixToString(C,choice);
disp(strcat('CIPHER TEXT:  ',cTxt));

disp('DECRYPTION STARTS');
%DES DECRYPTION
C = stringToMatrix(cTxt,choice);
a=5;
I = addRoundKey(C,K(41:44,:)');

for i=1:9
    I = invShiftRows(I);
    I = substituteBytes(I,InvSBox);
    ind = 4*(10-i) + 1;
    I = addRoundKey(I,K(ind:ind+3,:)');
    I = invMixColumns(I);
end

I = invShiftRows(I);
I = substituteBytes(I,InvSBox);
P = addRoundKey(I,K(1:4,:)');

pTxt = matrixToString(P,choice);
disp(strcat('DECRYPTED PLAIN TEXT:  ',pTxt));