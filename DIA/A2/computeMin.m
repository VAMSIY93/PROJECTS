function [me,mInd,valid] = computeMin(A)
    valid=1;
    B = sort(A);
    [~,c] = size(B);
    mInd=1;
    me=B(1);
    for i=1:c
        if B(i)>=0
            me = B(i);
            if me==A(2)
                mInd=2;
            elseif me==A(1)
                mInd=1;
            elseif c==3
                if me==A(3)
                    mInd=3;
                end
            end
            return;
        elseif i==3
            valid=0;
            return;
        end
    end
end