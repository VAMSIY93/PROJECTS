img1 = imread('color.png');
img2 = imread('gray.png');
I = rgb2gray(img2);
lab = rgb2lab(img1);
l = lab(:,:,1);
a = lab(:,:,2);
b = lab(:,:,3);

lum = l/100;

[h2,bin] = imhist(double(I)/255);
J = histeq(lum,bin);
N = imhistmatch(lum,J,256);
%{
fun = @(x) mean(x(:));
M = nlfilter(N,[5,5],'std2');
STD = M;
[r,c]=size(M);
omin = min(min(M(3:r-2,3:c-2)));
omax = max(max(M(3:r-2,3:c-2)));
M(3:r-2,3:c-2)=(M(3:r-2,3:c-2)-omin)/(omax-omin);
%}
B=N;
Map = zeros(256,3);
for i=1:256
    R = abs(B-bin(i,1));
    [r,c] = find(R==min(min(R)));
    if isempty(r)==0
        Map(i,1) = l(r(1,1),c(1,1));
        Map(i,2) = a(r(1,1),c(1,1));
        Map(i,3) = b(r(1,1),c(1,1));
    end
end

[r,c]=size(I);
Target = zeros(r,c,3);
for i=1:r
    for j=1:c
        Target(i,j,:)=Map(I(i,j)+1,:);
    end
end

RGB = lab2rgb(Target,'OutputType','uint8');
imshowpair(I,RGB,'montage');