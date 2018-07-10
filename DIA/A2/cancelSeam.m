function [seamImg,Gsum] = cancelSeam(Gsum,seamImg,img,k,row,col,saveGsums)
    [r,c] = size(Gsum);
    for i=row:r
        if i<r
            Gsum(i,col) = saveGsums(i,1);
        end
        seamImg(i,col,:) = img(i,col,:);
        if i<r && col>1 && Gsum(i+1,col-1)==-k
            col = col-1;
        elseif i<r && col<c && Gsum(i+1,col+1)==-k
            col = col+1;
        end 
    end
end