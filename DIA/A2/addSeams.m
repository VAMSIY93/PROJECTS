function img = addSeams(img,seams,seamImg)
    [r,c,~] = size(img);
    col = zeros(r,seams,3);
    img = [img col];
    seamImg = [seamImg col];
    for i=1:r
        cnt=0;
        for j=1:c+seams
            if (seamImg(i,j,1)==255 && seamImg(i,j,2)==0 && seamImg(i,j,3)==0)
                cnt=cnt+1;
                if j>1 && j<c
                    avg = (uint16(img(i,j-1,:))+uint16(img(i,j+1,:)))/2;
                elseif j==1
                    avg = img(i,j+1,:);
                else
                    avg = img(i,j-1,:);
                end
                img(i,1:c+cnt,:) = [img(i,1:j,:) avg(1,1,:) img(i,j+1:c+cnt-1,:)];
                seamImg(i,1:c+cnt,:) = [seamImg(i,1:j,:) avg(1,1,:) seamImg(i,j+1:c+cnt-1,:)];
            end
        end
    end
end