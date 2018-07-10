im3=imread('C.jpg');
imtool(im3);
lab3=rgb2lab(im3);
imtool(lab3(:,:,1),[]);
imtool(lab3(:,:,2),[]);
imtool(lab3(:,:,3),[]);