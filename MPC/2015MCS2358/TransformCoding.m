img = imread('lenna.jpg');
ycbcr = rgb2ycbcr(img);
Y = ycbcr(:,:,1);
I = Y;
[r,c] = size(I);
n = input('Enter block size: ');
cof = input('Enter number of coefficients: ');
xr = n-mod(r,n);
yr = n-mod(r,n);

if xr<n
    cols = ones(r,xr);
    last = double(I(:,c));
    for i=1:r
        cols(i,:) = last(i,1)*cols(i,:);
    end
    I = [I cols];
end

[r,c] = size(I);
if yr<n
    rows = ones(yr,c);
    last = double(I(r,:));
    for i=1:c
        rows(:,i) = last(:,i)*rows(:,i);
    end
    I = [I;rows];
end

[r,c] = size(I);
src = I;
DCI = zeros(r,c);
DFI = zeros(r,c);
I = double(I)-128;
Q = getQTable(n);
M = zeros(n);
A = zeros(n);

for i=1:n:r
    for j=1:n:c
        subI = I(i:i+n-1,j:j+n-1);
        X = int16(dct2(subI));
        S = X./Q;
        if(cof<=n)
            S = retainCoefficients(S,cof);
        end
        X = S.*Q;
        DCI(i:i+n-1,j:j+n-1) = int16(idct2(X));
        
        X = fft2(subI);
        for k=1:n
            for l=1:n
                M(k,l) = abs(X(k,l));
                A(k,l) = angle(X(k,l));
            end
        end
        M = int16(M);
        S = M./Q;
        if(cof<=n)
            S = retainCoefficients(S,cof);
        end
        S = S.*Q;
        S = double(S);
        Re = S.*cos(A);
        Im = S.*sin(A);
        X = complex(Re,Im);
        X = ifft2(X);
        DFI(i:i+n-1,j:j+n-1) = real(X);
    end
end

MSE_C = (sum(sum((DCI-I).^2)))/(r*c);
MSE_F = (sum(sum((DFI-I).^2)))/(r*c);
DCI = uint8(DCI+128);
DFI = uint8(DFI+128);

imshowpair(DCI,DFI,'montage');