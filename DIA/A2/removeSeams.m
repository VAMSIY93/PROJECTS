function img = removeSeams(img,seams,seamImg)
    [r,c,~] = size(img);
    imshow(seamImg);
    pause(2);
    for i=1:r
        for j=1:c
            while seamImg(i,j,1)==255 && seamImg(i,j,2)==0 && seamImg(i,j,3)==0
                img(i,j:c-1,:)=img(i,j+1:c,:);
                seamImg(i,j:c-1,:) = seamImg(i,j+1:c,:);

                if j>(c-seams)
                    break;
                end
            end
        end
    end
    img = img(:,1:c-seams,:);
    imshow(img);
    pause(2);
end