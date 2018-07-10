img = imread('source.png');
gray = rgb2gray(img);
tau=1;
phi=30.0/255;

gaus1 = fspecial('gaussian',21,0.5);
gaus2 = fspecial('gaussian',21,1);
dog = gaus1-tau*gaus2;
SegImg = conv2(double(gray),dog,'same');
eps=computeThreshold(SegImg);

[r,c] = size(SegImg);
Output = zeros(r,c);
for i=1:r
    for j=1:c
        if(SegImg(i,j)>eps)
            Output(i,j)=1;
        else
            Output(i,j)=1+tanh(phi*(SegImg(i,j)));
        end
    end
end
            
imshow(Output);