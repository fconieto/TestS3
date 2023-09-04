"use strict";
const pageDataTransfer = new DataTransfer();
var loadFiles = function (componentName) {
    var settings = {
        max_width: 3000,
        max_height: 3000,
        quality: 1
    };

    pageDataTransfer.clearData();

    const uploadFiles = document.getElementById(componentName).files;
    if (uploadFiles.length === 0) {
        return false;
    }

    for (var i = 0; i < uploadFiles.length; i++) {
        const uploadFile = uploadFiles[i];
        if (uploadFile && uploadFile.type.startsWith('image')) {
            var fileReader = new FileReader();
            fileReader.addEventListener("load", (frevent) => {
                const img = document.createElement('img');
                img.addEventListener("load", (imgevent) => {
                    var originalImg = imgevent.target;
                    if (originalImg.width < settings.max_width && originalImg.height < settings.max_height) {
                        pageDataTransfer.items.add(uploadFile);
                    } else {
                        var canvas = document.createElement('canvas');
                        var ctx = canvas.getContext('2d');
                        ctx.drawImage(originalImg, 0, 0);

                        const ratio = Math.min(settings.max_width / originalImg.width, settings.max_height / originalImg.height);
                        const width = Math.round(originalImg.width * ratio);
                        const height = Math.round(originalImg.height * ratio);
                        canvas.width = width;
                        canvas.height = height;
                        var ctx = canvas.getContext('2d');
                        ctx.drawImage(originalImg, 0, 0, width, height);
                        canvas.toBlob(function (blob) {
                            pageDataTransfer.items.add(new File([blob], uploadFile.name, {type: uploadFile.type}));
                        }, 'image/jpeg', settings.quality);
                    }
                });
                img.src = frevent.target.result;
            });

            fileReader.readAsDataURL(uploadFile);
        } else {
            pageDataTransfer.items.add(uploadFile);
        }
    }
};
