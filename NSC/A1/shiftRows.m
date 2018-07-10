function I = shiftRows(I)
    for i=2:4
        I(i,:) = circshift(I(i,:),1-i,2);
    end
end