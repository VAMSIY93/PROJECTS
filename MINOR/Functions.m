function img = Functions(img)
    [r,c]=size(img);
    rd=r/4;
    cd=c/4;
    for i=1:r-1
        if mod(i,rd)==0
            %img(i,:)=zeros(1,c);
            %img(i-1,:)=zeros(1,c);
            %img(i+1,:)=zeros(1,c);         
        end
    end
    
    for i=1:c-1
        if mod(i,cd)==0
            %img(:,i)=zeros(r,1);
            %img(:,i-1)=zeros(r,1);
            %img(:,i+1)=zeros(r,1);         
        end
    end
end
