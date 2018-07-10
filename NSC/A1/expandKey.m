function K = expandKey(key,SBox)
    K = zeros(44,4);
    K(1:4,:) = key;
    RC = zeros(1,10);
    RC(1,1) = 1;
    RCon = zeros(1,4);
    for i=2:10
        if(i==9)
            RC(1,i) = 27;
        else
            RC(1,i) = bitsll(RC(1,i-1),1);
        end
    end
    for i=5:44
        if mod(i,4)==1
            B = K(i-1,:);
            B = circshift(B,-1,2);
            for j=1:4           
                B(1,j) = mapBytes(B(1,j),SBox);
            end
            RCon(1,1) = RC(1,floor(i/4));
            B = bitxor(B,RCon);   
            K(i,:) = bitxor(K(i-4,:),B);
        else
            K(i,:) = bitxor(K(i-1,:),K(i-4,:));
        end
    end
end