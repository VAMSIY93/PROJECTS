function img = inverseImage(img)
    [r,c,~] = size(img);
    resImg = zeros(c,r,3);
    for i=1:3
        samp = img(:,:,i);
        resImg(:,:,i) = samp';
    end
    img = uint8(resImg);
end