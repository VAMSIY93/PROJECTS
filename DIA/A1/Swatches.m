img1 = imread('color.png');
img2 = imread('gray.png');
I = rgb2gray(img2);
img = imresize(img1,0.2);
lab = rgb2lab(img);
l = lab(:,:,1);
a = lab(:,:,2);
b = lab(:,:,3);

lum = l/100;
[r,c] = size(lum);
X = reshape(lum,r*c,1);
I = double(I)/255;
imshowpair(img1,I,'montage');
pause(5);
n = input('Enter number of swatches: ');
[idx,C] = kmeans(X,n);

Map = zeros(n,3);
for i=1:n
    R = abs(lum-C(i,1));
    [r,c] = find(R==min(min(R)));
    Map(i,:) = lab(r(1,1),c(1,1),:);
end
Map(:,1) = Map(:,1)/100;

[r,c] = size(I);
LAB = zeros(r,c,3);
for i=1:r
    for j=1:c
        R = abs(Map(:,1)-I(i,j));
        r1 = find(R==min(R));
        LAB(i,j,:) = Map(r1(1,1),:);
    end
end

LAB(:,:,1) = LAB(:,:,1)*100;
RGB = lab2rgb(LAB,'OutputType','uint8');
imshowpair(I,RGB,'montage');