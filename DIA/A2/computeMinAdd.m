function [me,mInd,valid] = computeMinAdd(A,R)
    B = sort(A);
    [~,c] = size(B);
    mInd=1;
    me=B(1);
    if me==A(2)
        mInd=2;
    elseif me==A(1)
        mInd=1;
    elseif c==3 && me==A(3)
        mInd=3;
    end
    if R(mInd)>=0
        valid=1;
    else
        valid=0;
    end
end