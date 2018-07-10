function InvSBox = generateInvSBox(MInv)
    InvSBox = zeros(16);
    for i=1:16
        for j=1:16
            InvSBox(i,j) = bitsll(i-1,4) + j-1;
        end
    end
    
    % B' = M*B + D
    n = bitsll(0,4) + 5;
    D = (de2bi(n,8))';
    
    n = bitsll(4,4) + 10;
    V = (de2bi(n,8))';
    M = zeros(8);
    M(:,1) = V;
    for i=2:8
        V = circshift(V,1);
        M(:,i) = V;
    end

    for i=1:16
        for j=1:16
            B = de2bi(InvSBox(i,j),8);
            B = B';
            B = mod(M*B,2);
            B = bitxor(B,D);
            InvSBox(i,j) = bi2de(B');
        end
    end
        
    % GF(2^8) - Inverse
    for i=1:16
        for j=1:16
            InvSBox(i,j) = MInv(uint8(InvSBox(i,j))+1,1);
        end
    end
    InvSBox(10,13) = 28;
end