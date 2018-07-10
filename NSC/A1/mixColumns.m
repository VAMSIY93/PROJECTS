function I = mixColumns(I)
    V = zeros(4);
    V(1,:) = [2 3 1 1];
    for i=2:4
        V(i,:) = circshift(V(i-1,:),1,2);
    end
      
    Oup = zeros(4);
    for i=1:4
        for j=1:4
            R = 0;
            for k=1:4
                temp = gmul(V(i,k),I(k,j));
                R = bitxor(R,temp);
            end
            Oup(i,j) = R;
        end
    end
    I = Oup;
end
