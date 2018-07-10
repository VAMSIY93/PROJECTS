function regNo = getStem(img)
    [r,c]=size(img);
    rd=r/4;
    cd=c/4;
    regCount=zeros(16,1);
    for i=1:r-1
        for j=1:c-1
            if img(i,j)==0
                index = floor(i/rd)*4 + ceil(j/cd);
                index = uint8(index);
                regCount(index,1)=regCount(index,1)+1;
            end
        end
    end
    %disp(regCount);
    count=regCount(1,1);
    regNo=1;
    for i=2:16
        if count<regCount(i,1)
            count=regCount(i,1);
            regNo=i;
        end
    end
end