im1=imread('A.jpg');
im2=imread('B.jpg');
im3=imread('C.jpg');
lab1=rgb2lab(im1);
lab2=rgb2lab(im2);
lab3=rgb2lab(im3);
%imshow(im2);
%imshow(lab1);


E1=edge(g1);
E2=im2bw(g1);
[E3,~]=histeq(g1);
E4=im2bw(E3);
[r,c]=size(g1);
I1=zeros(r,c);
bin1=im2bw(im1);
imshow(bin)
%imshow(g1)
%hold on
%imhist(g1);
imshow(lab(:,:,1),[0 100])
