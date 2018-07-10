function SBox = generateSBox(MInv)
    SBox = zeros(16);
    for i=1:16
        for j=1:16
            SBox(i,j) = bitsll(i-1,4) + j-1;
        end
    end
    
    % GF(2^8) - Inverse
    for i=1:16
        for j=1:16
            SBox(i,j) = MInv(uint8(SBox(i,j))+1,1);
        end
    end
    
    % B' = M*B + C;
    n = bitsll(6,4) + 3;
    C = (de2bi(n,8))';
    
    n = bitsll(1,4) + 15;
    V = (de2bi(n,8))';
    M = zeros(8);
    M(:,1) = V;
    for i=2:8
        V = circshift(V,1);
        M(:,i) = V;
    end
    
    for i=1:16
        for j=1:16
            B = de2bi(SBox(i,j),8);
            B = B';
            B = mod(M*B,2);
            B = bitxor(B,C);
            SBox(i,j) = bi2de(B');
        end
    end 
    SBox(end,end) = 22;
end