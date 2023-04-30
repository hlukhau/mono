window.onload = function() {
  //console.log("initDrag");
  initDragElement();
  //console.log("initResize");
  initResizeElement();
  //console.log("init");
};


function initDragElement() {
  var pos1 = 0,
    pos2 = 0,
    pos3 = 0,
    pos4 = 0;
  var popups = document.getElementsByClassName("popup");
  var elmnt = null;
  var currentZIndex = 100; //TODO reset z index when a threshold is passed

  for (var i = 0; i < popups.length; i++) {
    var popup = popups[i];
    var header = getHeader(popup);

    popup.onmousedown = function() {
      this.style.zIndex = "" + ++currentZIndex;
    };

    if (header) {
      header.parentPopup = popup;
      header.onmousedown = dragMouseDown;
    }
  }

  function dragMouseDown(e) {
    elmnt = this.parentPopup;
    elmnt.style.zIndex = "" + ++currentZIndex;

    e = e || window.event;
    // get the mouse cursor position at startup:
    pos3 = e.clientX;
    pos4 = e.clientY;
    document.onmouseup = closeDragElement;
    // call a function whenever the cursor moves:
    document.onmousemove = elementDrag;
  }

  function elementDrag(e) {
    if (!elmnt) {
      return;
    }

    e = e || window.event;
    // calculate the new cursor position:
    pos1 = pos3 - e.clientX;
    pos2 = pos4 - e.clientY;
    pos3 = e.clientX;
    pos4 = e.clientY;
    // set the element's new position:
    elmnt.style.top = elmnt.offsetTop - pos2 + "px";
    elmnt.style.left = elmnt.offsetLeft - pos1 + "px";
  }

  function closeDragElement() {
    /* stop moving when mouse button is released:*/
    document.onmouseup = null;
    document.onmousemove = null;
  }

  function getHeader(element) {
    var headerItems = element.getElementsByClassName("popup-header");

    if (headerItems.length === 1) {
      return headerItems[0];
    }

    return null;
  }
}

function initResizeElement() {
  var popups = document.getElementsByClassName("popup");
  var element = null;
  var startX, startY, startWidth, startHeight;

  for (var i = 0; i < popups.length; i++) {
    var p = popups[i];

    var right = document.createElement("div");
    right.className = "resizer-right";
    p.appendChild(right);
    right.addEventListener("mousedown", initDrag, false);
    right.parentPopup = p;

    var bottom = document.createElement("div");
    bottom.className = "resizer-bottom";
    p.appendChild(bottom);
    bottom.addEventListener("mousedown", initDrag, false);
    bottom.parentPopup = p;

    var both = document.createElement("div");
    both.className = "resizer-both";
    p.appendChild(both);
    both.addEventListener("mousedown", initDrag, false);
    both.parentPopup = p;
  }

  function initDrag(e) {
    element = this.parentPopup;

    startX = e.clientX;
    startY = e.clientY;
    startWidth = parseInt(
      document.defaultView.getComputedStyle(element).width,
      10
    );
    startHeight = parseInt(
      document.defaultView.getComputedStyle(element).height,
      10
    );
    document.documentElement.addEventListener("mousemove", doDrag, false);
    document.documentElement.addEventListener("mouseup", stopDrag, false);
  }

  function doDrag(e) {
    element.style.width = startWidth + e.clientX - startX + "px";
    element.style.height = startHeight + e.clientY - startY + "px";
  }

  function stopDrag() {
    document.documentElement.removeEventListener("mousemove", doDrag, false);
    document.documentElement.removeEventListener("mouseup", stopDrag, false);
  }
}


var images = new Map;

function timeout(ms, promise) {
  return new Promise(function(resolve, reject) {
    setTimeout(function() {
      reject(new Error("timeout"))
    }, ms)
    promise.then(resolve, reject)
  })
}

function sendtext() {
    var text = document.getElementById("chat_text");


    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/send');
    xhr.setRequestHeader('Content-Type', 'text/plain;charset=UTF-8');
    xhr.send(text.value);
    text.value = ''
}

setInterval(function() {
     timeout(100, fetch('/chat', { mode: 'no-cors' })).then(function(response) {
        response.text().then(function(result) {
            var div = document.getElementById("chat_body");
            div.innerHTML = result;
            console.log(result);
        });
     }).catch(function(error) {});
}, 1000);



setInterval(function() {
    var list = document.querySelectorAll("img");

    list.forEach(function(image) {

        if (image.id != "static") {

            var name = images.get(image);
            if (name == null) {
                name = image.alt;
                images.set(image, name);
            }
            var s = name + "?" + new Date().getTime();

            var index = s.lastIndexOf("/i");
            var name = s;

            if (index != -1) {
                name = name.substring(0, index);
                name += "/name";
            }

            timeout(100, fetch(name, { mode: 'no-cors' })).then(function(response) {
                image.src = s;
            }).catch(function(error) {});
        }
    });
}, 500);

var save = document.getElementById('saveId');

save.addEventListener('click', function() {
    var list = document.querySelectorAll(".popup");
    var json = "[\n";
    var add = "";

    list.forEach(function(div) {
        var img = div.querySelector("img");
        var head = div.querySelector(".popup-header");

        var style = div.style;
        var width = div.clientWidth;
        var height = div.clientHeight;
        var record = "top: " + style.top + "; left: " + style.left + "; width: " + width + "px; height: " + height + "px;";
        var src = img.src;

        var index = src.indexOf("?");
        var url = src;

        if (index != -1) {
            url = src.substring(0, index);
        }

        json += add + "{\"name\": \"" + head.innerText + "\", \"url\": " + "\"" + url + "\", \"style\": \"" + record + "\"}\n";
        add = ", ";
    });

    json += "]";

    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/save');
    xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');
    xhr.send(json);
});
