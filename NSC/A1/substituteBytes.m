function I = substituteBytes(I,SBox)
    for i=1:4
        for j=1:4
            I(i,j) = mapBytes(I(i,j),SBox);
        end
    end
end