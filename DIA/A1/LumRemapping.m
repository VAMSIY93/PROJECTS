img1 = imread('color.png');
img2 = imread('gray.png');
I = rgb2gray(img2);
img3 = imresize(img1,0.2);
lab = rgb2lab(img3);
l = lab(:,:,1);
a = lab(:,:,2);
b = lab(:,:,3);

lum = l/100;
Int = double(I)/255;
Mb = mean(mean(Int));
Sb = std2(Int);
Ma = mean(mean(lum));
Sa = std2(lum);

nlum = (Sb/Sa)*(lum-Ma) + Mb;
omin = min(min(nlum));
omax = max(max(nlum));
nlum=(nlum-omin)/(omax-omin);

[r,c] = size(I);
Target = zeros(r,c,3);
for i=1:r
    for j=1:c
        R = abs(nlum-Int(i,j));
        [r1,c1] = find(R==min(min(R)));
        Target(i,j,1) = Int(i,j)*100;
        Target(i,j,2:3) = lab(uint16(r1(1,1)),uint16(c1(1,1)),2:3);
    end
end

RGB = lab2rgb(Target,'OutputType','uint8');
imshowpair(I,RGB,'montage');