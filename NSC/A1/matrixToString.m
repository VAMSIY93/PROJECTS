function cTxt = matrixToString(C,choice)
    C = reshape(C,1,16);
    cTxt = '';
    if choice==1
        for i=1:16
            cTxt = strcat(cTxt,char(C(1,i)));
        end
    else
        for i=1:16
            n = C(1,i);
            c = bitand(n,bitsll(15,4));
            c = bitsra(c,4);
            cTxt = strcat(cTxt, decimalToHex(c));
            c = bitand(n,15);
            cTxt = strcat(cTxt, decimalToHex(c));
            
        end
    end
end