function P = stringToMatrix(inp,choice)
    P = zeros(16,1);
    if choice==1
        for i=1:16
             P(i,1) = uint8(inp(i));
        end
        P = reshape(P,4,4);
    else
        for i=1:2:32
            mhb = hexToDecimal(uint8(inp(i)));
            lhb = hexToDecimal(uint8(inp(i+1)));
            n = bitsll(mhb,4) + lhb;
            ind = ceil(i/2);
            P(ind,1) = n;
        end
        P = reshape(P,4,4);
    end
end