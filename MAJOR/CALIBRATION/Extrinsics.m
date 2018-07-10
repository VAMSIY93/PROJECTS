W = zeros(6,4);
W(1,:)=[38,76,0,1];
W(2,:)=[190,38,0,1];
W(3,:)=[76,152,0,1];
W(4,:)=[304,114,0,1];
W(5,:)=[266,76,0,1];
W(6,:)=[152,152,0,1];

I = zeros(6,3);
I(1,:)=[250,166,1];
I(2,:)=[327,139,1];
I(3,:)=[270,204,1];
I(4,:)=[395,175,1];
I(5,:)=[371,156,1];
I(6,:)=[309,202,1];

load('cameraP.mat');
RI = cameraParamsDepth.RotationMatrices(:,:,2)';
TI = cameraParamsDepth.TranslationVectors(2,:)';
II = cameraParamsDepth.IntrinsicMatrix';
O = [0,0,0];
EI = [RI,TI;O,1];

RC = cameraParamsColor.RotationMatrices(:,:,2)';
RV = cameraParamsColor.RotationVectors(2,:);
%NRC = rodigues(RV);
TC = cameraParamsColor.TranslationVectors(2,:)';
IC = cameraParamsColor.IntrinsicMatrix';
O = [0,0,0];
EC = [RC,TC;O,1];

CCI = EI*W';
CCC = EC*W';

M=zeros(4,4);
M(4,4)=1;
for i=1:3
    A = CCI(:,1:4)';
    B = CCC(i,1:4)';
    M(i,:)=(A\B)';
end

R = M(1:3,1:3);
T = M(1:3,4);

NCC = M*CCI;
ICC = IC*NCC(1:3,:);
for i=1:6
    ICC(:,i) = ICC(:,i)/ICC(3,i);
end

depths = imread('depth2.png');
depth=double(depths);
CCD=zeros(6,4);
for i=1:6
    CCD(i,1:3)=[(I(i,1)-II(1,3))/II(1,1), (I(i,2)-II(2,3))/II(2,2),1]*depth(uint16(I(i,1)),uint16(I(i,2)));
    CCD(i,4)=1;
end

ClC = M*CCD';
ICl = IC*ClC(1:3,:);
for i=1:6
    ICl(:,i) = ICl(:,i)/ICl(3,i);
end
