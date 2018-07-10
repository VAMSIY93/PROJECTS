function I = addRoundKey(P,K)
    I = bitxor(P,K);
end