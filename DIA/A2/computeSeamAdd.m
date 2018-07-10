function [Gsum,seamImg,E,cancel,finish] = computeSeamAdd(Gsum,E,k,seamImg,img)
    [r,c] = size(E);
    A = sort(E(r,:));
    col = 2;
    cancel = 0;
    finish = 0;
    saveGsums = zeros(r,1);
    for i=1:c
        if A(i)>=0
            me = A(i);
            cols = find(E(r,:)==me);
            col = cols(1);
            break;
        elseif i==c
            finish=1;
        end
    end
    saveGsums(r,1) = Gsum(r,col);
    Gsum(r,col) = -k;
    E(r,col) = -k;
    seamImg(r,col,1)=255;
    seamImg(r,col,2)=0;
    seamImg(r,col,3)=0;
    for i=r:-1:2
        if col>1 && col<c
            [~,mInd,valid] = computeMinAdd(E(i-1,col-1:col+1),Gsum(i-1,col-1:col+1));
            if valid==1
                col = col+mInd-2;
            else
                [seamImg,Gsum] = cancelSeam(Gsum,seamImg,img,k,i,col,saveGsums);
                cancel=1;
                return;
            end
        elseif col==1
            [~,mInd,valid] = computeMinAdd(E(i-1,col:col+1),Gsum(i-1,col:col+1));
            if valid==1
                col = col+mInd-1;
            else
                [seamImg,Gsum] = cancelSeam(Gsum,seamImg,img,k,i,col,saveGsums);
                cancel=1;
                return;
            end
        else
            [~,mInd,valid] = computeMinAdd(E(i-1,col-1:col),Gsum(i-1,col-1:col));
            if valid==1
                col = col+mInd-2;
            else
                [seamImg,Gsum] = cancelSeam(Gsum,seamImg,img,k,i,col,saveGsums);
                cancel=1;
                return;
            end
        end
        saveGsums(i-1,1) = Gsum(i-1,col);
        Gsum(i-1,col)=-k;
        seamImg(i-1,col,1)=255;
        seamImg(i-1,col,2)=0;
        seamImg(i-1,col,3)=0;
    end
    imshow(seamImg);
    title('Seams');
end