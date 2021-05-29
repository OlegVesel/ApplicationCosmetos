
//скрипт который опускается вниз страницы после обновления
    document.addEventListener("DOMContentLoaded", function(event) {

           if (localStorage.getItem('height') != null){
                window.scrollTo(0, localStorage.getItem('height'))
                localStorage.removeItem('height')
           }

    });
//скрипт для увеличения числа элементов отображаемых на странице
    function seeMore(count, currentSize, maxSize){
        let url = window.location.href  //получаем текущую ссылку
        let newSize = 0
        if (count < maxSize)
           newSize  = currentSize+count
        else newSize = maxSize

            /*здесь берется url страницы на которой мы находимся и
            добавляется к нему "?size="+новое колличество отображаемых строк
            например http://192.168.0.104/app до сюда 24 символа потом плюс  ?size=45
            */
            let newUrl = url.slice(0,24)+"?size="+newSize

        if(currentSize >= maxSize){
            document.getElementById("showMoreBtn").innerText="Больше ничего нет"
            document.getElementById("showMoreBtn").classList.add('disabled')
        } else {
           localStorage.setItem('height', pageYOffset) //запоминает текущие координаты по оси Y чтобы прокрутить страницу на это же место
           location.href = newUrl
        }
 }

