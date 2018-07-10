img = imread('image1.jpg');
[r,c,~] = size(img);
res = zeros(c,r,3);
for i=1:3
    samp = img(:,:,i);
    imshow(samp');
    pause(1);
    res(:,:,i) = samp';
end
I = rgb2gray(img);
I = I';
imshow(uint8(res));