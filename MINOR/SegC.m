%im3b=(lab3(:,:,3));
[r3,c3]=size(im1);

samp=zeros(r3,c3);
for i=1:r3
    for j=1:c3
        if im1(i,j)>190
            samp(i,j)=1;
        end
    end
end

imtool(samp);
