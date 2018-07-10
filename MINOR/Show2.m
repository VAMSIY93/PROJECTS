im2=imread('B.jpg');
imtool(im2);
lab2=rgb2lab(im2);
imtool(lab2(:,:,1),[]);
imtool(lab2(:,:,2),[]);
imtool(lab2(:,:,3),[]);