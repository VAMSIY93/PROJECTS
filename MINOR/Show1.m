im1=imread('C.jpg');
imtool(im1);
lab1=rgb2lab(im1);
imtool(rgb2gray(im1));
imtool(im2bw(im1));
imtool(lab1(:,:,1),[]);
imtool(lab1(:,:,2),[]);
imtool(lab1(:,:,3),[]);