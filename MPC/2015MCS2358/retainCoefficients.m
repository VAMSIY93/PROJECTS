function S = retainCoefficients(S,cof)
    [r,c] = size(S);
    R = zeros(r,c);
    for i=1:r
        for j=1:c
            if((i+j)<=(cof+1))
                R(i,j) = S(i,j);
            end
        end
    end
    S = int16(R);
end