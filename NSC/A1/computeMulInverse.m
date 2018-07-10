function MInv = computeMulInverse(M)
    [r,~] = size(M);
    MInv = zeros(r,1);
    for i=2:r
        [~,MInv(i,1)] = find(M(i,:)==1);
    end
    MInv(2:end,1) = MInv(2:end) - 1;
end    