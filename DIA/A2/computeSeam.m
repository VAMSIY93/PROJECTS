function [Gsum,seamImg] = computeSeam(Gsum,E,k,seamImg)
    [r,c] = size(E);
    A = sort(E(r,:));
    col = 2;
    for i=1:c
        if A(i)>=0
            me = A(i);
            cols = find(E(r,:)==me);
            col = cols(1);
            break;
        end
    end
    Gsum(r,col)=-k;
    seamImg(r,col,1)=255;
    seamImg(r,col,2)=0;
    seamImg(r,col,3)=0;
    for i=r:-1:2
        if col>1 && col<c
            [~,mInd,valid] = computeMin(E(i-1,col-1:col+1));
            if valid==1
                col = col+mInd-2;
            else
                return;
            end
        elseif col==1
            [~,mInd,valid] = computeMin(E(i-1,col:col+1));
            if valid==1
                col = col+mInd-1;
            else
                return;
            end
        else
            [~,mInd,valid] = computeMin(E(i-1,col-1:col));
            if valid==1
                col = col+mInd-2;
            else
                return;
            end
        end
        
        Gsum(i-1,col)=-k;
        seamImg(i-1,col,1)=255;
        seamImg(i-1,col,2)=0;
        seamImg(i-1,col,3)=0;
    end
    imshow(seamImg);
    title('Seams');
end