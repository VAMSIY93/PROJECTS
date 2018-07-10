function Gsum = computeGradient(img)
    gray = (img(:,:,1)+img(:,:,2)+img(:,:,3))/3;
    [Gx,Gy] = imgradientxy(gray,'intermediate');
    Gsum = abs(Gx)+abs(Gy);
    Gsum = Gsum*2;
end