//функция для раскрашивания разным цветом строк таблицы
function colorRow(id, date){

    let rowTable = document.getElementById(id)

    let now = new Date;     //получаем текущую дату
    let dateDeath = new Date(date)      //получаем дату выброса продукта

    let timeDiff = dateDeath.getTime() - now.getTime()      //получаем разницу между датами в миллисекундах
    let diffDays = Math.ceil(timeDiff / (1000 * 3600 * 24));    //переводим мс в дни, Math.ceil округляет вверх до ближайшего большего целого

    if (diffDays < 0)
            rowTable.style.background = "rgba(255, 0, 0, 0.4)"
        else
            if(diffDays>=0 && diffDays< 25)
        	    rowTable.style.background = "rgba(255, 87, 51, 0.2)"

            else
                if(diffDays>=25 && diffDays< 50)
        	    rowTable.style.background = "rgba(255, 158, 0, 0.2)"

                else
                    if(diffDays>=50 && diffDays< 75)
        	        rowTable.style.background = "rgba(244, 255, 0, 0.2)"

                    else
                        if(diffDays>=75 && diffDays< 100)
        	            rowTable.style.background = "rgba(30, 255, 0, 0.2)"

                        else
                        if(diffDays>=100)
        	            rowTable.style.background = "rgba(0, 123, 255, 0.2)"

    }


function changeParametersSorting(){
    let select = document.getElementsByName('sortParameter'); //найдем наш select в котором лежат критерии сортировки
    let parameter = select[0].options[select[0].selectedIndex].value; //возьмем из выбранного пункта значение (name, brand, etc..)

    let url = new URL(document.URL); //создадим объект url с нашей странички

    url.searchParams.set('sortBy', parameter);
    localStorage.setItem('sortParameter', parameter); //запишем параметр в localStorage, чтобы потом восстановить select
    window.location.href = url //обновим страничку с нашим параметром и заданным значением

}
function changeDirectionSorting(direction){
    //asc - true
    //desc - false
    //для удобства на бэке, чтобы не плодить if
    let url = new URL(document.URL); //создадим объект url с нашей странички
    if (direction === 'asc')
       url.searchParams.set('direction', 'true');
    else
       url.searchParams.set('direction', 'false');
    localStorage.setItem('sortDirection', direction); //запишем параметр в localStorage, чтобы потом восстановить select
    window.location.href = url //обновим страничку с нашим параметром и заданным значением

}
