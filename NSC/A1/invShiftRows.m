function I = invShiftRows(I)
    for i=2:4
        I(i,:) = circshift(I(i,:),i-1,2);
    end
end