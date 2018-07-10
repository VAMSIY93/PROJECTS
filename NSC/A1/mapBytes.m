function n = mapBytes(n,SBox)
    c = bitand(n,15);
    r = bitand(n,bitsll(15,4));
    r = bitsra(r,4);
    n = SBox(r+1,c+1);
end