(this["webpackJsonpentando-ootb-widgets"]=this["webpackJsonpentando-ootb-widgets"]||[]).push([[0],{56:function(e,t,a){e.exports=a(73)},61:function(e,t,a){},66:function(e,t,a){},69:function(e,t,a){},70:function(e,t,a){},71:function(e,t,a){},72:function(e,t,a){},73:function(e,t,a){"use strict";a.r(t);var n=a(0),r=a.n(n),c=a(11),i=a.n(c),o=(a(61),a(5)),l=a(6),u=a(27),s=a(7),d=a(8),h=a(50),g=a(4),p=a(77),m=a(74),v=a(75),f=a(90),b=a(76),E=a(78),O=a(79),_=a(80),k=a(81),y=a(82),S=[{code:"EN",descr:"English"},{code:"IT",descr:"Italian"}],C=[{code:"homepage",title:"Home",level:"0",url:"http://localhost:8090/entando-de-app/en/homepage.page",voidPage:!0},{code:"my_page",title:"My Page",level:"1",url:"http://localhost:8090/entando-de-app/en/my_page.page",voidPage:!1},{code:"my_homepage",title:"Home - test",level:"1",url:"http://localhost:8090/entando-de-app/en/my_homepage.page",voidPage:!1},{code:"sitemap",title:"Sitemap",level:"2",url:"http://localhost:8090/entando-de-app/en/sitemap.page",voidPage:!1},{code:"homepage",title:"Home",level:"0",url:"http://localhost:8090/entando-de-app/en/homepage.page",voidPage:!0},{code:"my_page",title:"My Page",level:"0",url:"http://localhost:8090/entando-de-app/en/my_page.page",voidPage:!1},{code:"my_homepage",title:"Home - test",level:"1",url:"http://localhost:8090/entando-de-app/en/my_homepage.page",voidPage:!0},{code:"sitemap",title:"Sitemap",level:"2",url:"http://localhost:8090/entando-de-app/en/sitemap.page",voidPage:!1},{code:"my_pagesdfdf",title:"My Page Wa",level:"2",url:"http://localhost:8090/entando-de-app/en/my_page.page",voidPage:!1},{code:"gbadoasd",title:"Forever",level:"3",url:"http://localhost:8090/entando-de-app/en/my_page.page",voidPage:!1},{code:"contact",title:"Contact",level:"2",url:"http://localhost:8090/entando-de-app/en/contact.page",voidPage:!1},{code:"address",title:"Address",level:"3",url:"http://localhost:8090/entando-de-app/en/address.page",voidPage:!1},{code:"about",title:"About",level:"1",url:"http://localhost:8090/entando-de-app/en/homepage.page",voidPage:!0},{code:"homepage",title:"Home",level:"0",url:"http://localhost:8090/entando-de-app/en/homepage.page",voidPage:!0}],P=function(e){Object(s.a)(a,e);var t=Object(d.a)(a);function a(e){var n;return Object(o.a)(this,a),(n=t.call(this,e)).state={expanded:!1},n.handleExpandSidePanel=n.handleExpandSidePanel.bind(Object(u.a)(n)),n}return Object(l.a)(a,[{key:"handleExpandSidePanel",value:function(){this.setState((function(e){return{expanded:!e.expanded}}))}},{key:"render",value:function(){return r.a.createElement(h.a,null,r.a.createElement(m.a,{"aria-label":"Entando OOTB Widgets Dev"},r.a.createElement(v.a,{href:"#",prefix:"Entando"},"OOTB Widgets Dev"),r.a.createElement(f.a,null,r.a.createElement(b.a,{"aria-label":"Components List",onClick:this.handleExpandSidePanel},r.a.createElement(p.a,null))),r.a.createElement(E.a,{"aria-label":"Header Panel",expanded:this.state.expanded},r.a.createElement(O.a,{"aria-label":"Switcher Container"},r.a.createElement(_.a,{"aria-label":"Language Chooser",href:"/"},"Language Chooser"),r.a.createElement(_.a,{href:"/nav-bar","aria-label":"Navigation Bar"},"Navigation Bar"),r.a.createElement(_.a,{href:"/login-button","aria-label":"Login Button"},"Login Button"),r.a.createElement(_.a,{href:"/search-bar","aria-label":"Search Bar"},"Search Bar"),r.a.createElement(_.a,{href:"/header-fragment","aria-label":"Header Fragment"},"Header Fragment"),r.a.createElement(k.a,null)))),r.a.createElement(y.a,null,r.a.createElement(g.a,{path:"/",exact:!0,render:function(){return r.a.createElement("choose-language-widget",{"current-lang":"EN",languages:JSON.stringify(S)})}}),r.a.createElement(g.a,{path:"/login-button",exact:!0,render:function(){return r.a.createElement("login-button-widget",{"app-url":Object({NODE_ENV:"production",PUBLIC_URL:"",WDS_SOCKET_HOST:void 0,WDS_SOCKET_PATH:void 0,WDS_SOCKET_PORT:void 0}).REACT_APP_BASEURL,page:"".concat(Object({NODE_ENV:"production",PUBLIC_URL:"",WDS_SOCKET_HOST:void 0,WDS_SOCKET_PATH:void 0,WDS_SOCKET_PORT:void 0}).REACT_APP_BASEURL,"en/homepage.page")})}}),r.a.createElement(g.a,{path:"/nav-bar",exact:!0,render:function(){return r.a.createElement(m.a,{"aria-label":"Entando Navigation Bar"},r.a.createElement("navigation-bar-widget",{"current-page":"homepage","nav-items":JSON.stringify(C)}))}}),r.a.createElement(g.a,{path:"/search-bar",exact:!0,render:function(){return r.a.createElement("search-bar-widget",{"action-url":Object({NODE_ENV:"production",PUBLIC_URL:"",WDS_SOCKET_HOST:void 0,WDS_SOCKET_PATH:void 0,WDS_SOCKET_PORT:void 0}).REACT_APP_BASEURL,placeholder:"Search"})}}),r.a.createElement(g.a,{path:"/header-fragment",exact:!0,render:function(){return r.a.createElement("header-fragment",{"app-url":Object({NODE_ENV:"production",PUBLIC_URL:"",WDS_SOCKET_HOST:void 0,WDS_SOCKET_PATH:void 0,WDS_SOCKET_PORT:void 0}).REACT_APP_BASEURL},r.a.createElement("template",null,r.a.createElement("navigation-bar-widget",{"current-page":"homepage","nav-items":JSON.stringify(C)}),r.a.createElement("choose-language-widget",{"current-lang":"EN",languages:JSON.stringify(S)}),r.a.createElement("login-button-widget",{"app-url":Object({NODE_ENV:"production",PUBLIC_URL:"",WDS_SOCKET_HOST:void 0,WDS_SOCKET_PATH:void 0,WDS_SOCKET_PORT:void 0}).REACT_APP_BASEURL,page:"".concat(Object({NODE_ENV:"production",PUBLIC_URL:"",WDS_SOCKET_HOST:void 0,WDS_SOCKET_PATH:void 0,WDS_SOCKET_PORT:void 0}).REACT_APP_BASEURL,"en/my_homepage.page")})))}})))}}]),a}(n.Component),j=a(16),T=a(24),N=a(89),L=a(91),A=a(83),R=a(84),w=(a(66),function(e){var t=e.languages,a=e.currentLang;return r.a.createElement(N.a,{renderIcon:function(){return r.a.createElement(r.a.Fragment,null,r.a.createElement(A.a,null),r.a.createElement(R.b,{className:"chooseLanguage__menu-arrow"}))},flipped:!0,className:"chooseLanguage"},t.map((function(e){return r.a.createElement(L.a,{key:e.code,itemText:e.descr,href:e.url,className:["langItem"].concat(Object(T.a)(a===e.code?["active"]:[])).join(" ")})})))});w.defaultProps={languages:[],currentLang:""};var U=w,B={languages:"languages",currentLang:"current-lang"},D=function(e){Object(s.a)(a,e);var t=Object(d.a)(a);function a(){return Object(o.a)(this,a),t.apply(this,arguments)}return Object(l.a)(a,[{key:"attributeChangedCallback",value:function(e,t,a){if(!Object.values(B).includes(e))throw new Error("Untracked changed attribute: ".concat(e));this.mountPoint&&a!==t&&this.render()}},{key:"connectedCallback",value:function(){this.mountPoint=document.createElement("div"),this.appendChild(this.mountPoint),this.render()}},{key:"render",value:function(){var e=JSON.parse(this.getAttribute(B.languages)),t=this.getAttribute(B.currentLang);i.a.render(r.a.createElement(U,{languages:e,currentLang:t}),this.mountPoint)}}],[{key:"observedAttributes",get:function(){return Object.values(B)}}]),a}(Object(j.a)(HTMLElement));customElements.get("choose-language-widget")||customElements.define("choose-language-widget",D);var x=a(38),I=a(53),K=a.n(I),H=a(39),M=a(85),W=a(86),q=a(87),J=a(20),V=a(3),F=a.n(V),z=function(e,t){for(var a=0;a<t.length;a++)if(G(e,t[a]))return!0;return!1},G=function(e){var t=arguments.length>1&&void 0!==arguments[1]?arguments[1]:{},a=t.key,n=t.which,r=t.keyCode;return"string"===typeof e?e===a:"number"===typeof e?e===n||e===r:e.key&&Array.isArray(a)?-1!==a.indexOf(e.key):e.key===a||e.which===n||e.keyCode===r},Q={key:"Enter",which:13,keyCode:13},X={key:["Escape","Esc"],which:27,keyCode:27},Y={key:" ",which:32,keyCode:32},Z=a(2),$=a.n(Z),ee=a(40),te=a(18),ae=(function(e){var t=Object.keys(e);t.reduce((function(t,a){return Object(te.a)(Object(te.a)({},t),{},Object(ee.a)({},a,(n=e[a],function(e,t,a){for(var r=arguments.length,c=new Array(r>3?r-3:0),i=3;i<r;i++)c[i-3]=arguments[i];return n.apply(void 0,[e,t,a].concat(c))})));var n}),{})}({"aria-label":$.a.string,"aria-labelledby":$.a.string}),J.a.prefix),ne=function(e){Object(s.a)(a,e);var t=Object(d.a)(a);function a(e){var n;return Object(o.a)(this,a),(n=t.call(this,e))._subMenus=r.a.createRef(),n.handleOnIconClick=function(e){e.stopPropagation(),e.preventDefault(),n.setState((function(e){return{expanded:!e.expanded}}))},n.handleOnClick=function(e){if("#"===n.props.href){var t=n._subMenus.current;t&&t.contains(e.target)||e.preventDefault(),n.setState((function(e){return{expanded:!e.expanded}}))}},n.handleOnKeyDown=function(e){if(z(e,[Q,Y]))return e.stopPropagation(),e.preventDefault(),void n.setState((function(e){return{expanded:!e.expanded}}))},n.handleOnBlur=function(e){if(null!==e.relatedTarget){var t=e.relatedTarget.closest("li.".concat(ae,"--header__submenu")),a=e.currentTarget;if(t&&(a.dataset.uniqueId===t.dataset.uniqueId||a.querySelector("li[data-unique-id='".concat(t.dataset.uniqueId,"']"))))return void e.stopPropagation()}var r=n.items.find((function(t){return t===e.relatedTarget}));e.relatedTarget&&(e.relatedTarget.getAttribute("href")&&"#"!==e.relatedTarget.getAttribute("href")||r)||n.setState({expanded:!1,selectedIndex:null})},n.handleMenuButtonRef=function(e){n.props.focusRef&&n.props.focusRef(e),n.menuButtonRef=e},n.handleItemRef=function(e){return function(t){n.items[e]=t}},n.handleMenuClose=function(e){if(z(e,[X])&&n.state.expanded)return e.stopPropagation(),e.preventDefault(),n.setState((function(){return{expanded:!1,selectedIndex:null}})),void n.menuButtonRef.focus()},n._renderMenuItem=function(e,t){if(r.a.isValidElement(e))return r.a.cloneElement(e,{ref:n.handleItemRef(t)})},n.state={expanded:!1,selectedIndex:null},n.items=[],n}return Object(l.a)(a,[{key:"render",value:function(){var e=this.props,t=e["aria-label"],a=e["aria-labelledby"],n=e.className,c=e.children,i=e.renderMenuContent,o=e.menuLinkName,l=e.href,u=e.uniqueId,s={"aria-label":t,"aria-labelledby":a},d=F()("".concat(ae,"--header__submenu"),n);return r.a.createElement("li",{className:d,onKeyDown:this.handleMenuClose,onClick:this.handleOnClick,tabIndex:0,"data-unique-id":u,onBlur:this.handleOnBlur},r.a.createElement("a",Object.assign({className:"".concat(ae,"--header__menu-item ").concat(ae,"--header__menu-title"),href:l,"aria-expanded":this.state.expanded},s),o,r.a.createElement("button",{"aria-haspopup":"menu",onClick:this.handleOnIconClick,onKeyDown:this.handleOnKeyDown,ref:this.handleMenuButtonRef},r.a.createElement(i,null))),r.a.createElement("ul",Object.assign({},s,{ref:this._subMenus,className:"".concat(ae,"--header__menu")}),r.a.Children.map(c,this._renderMenuItem)))}}]),a}(n.Component);ne.defaultProps={renderMenuContent:function(){return r.a.createElement(R.a,{className:"".concat(ae,"--header__menu-arrow")})},href:"#",uniqueId:""};var re=r.a.forwardRef((function(e,t){return r.a.createElement(ne,Object.assign({},e,{focusRef:t}))}));re.displayName="HeaderMenu";var ce=re,ie=a(41),oe=a(54),le=Object(oe.a)(H.withKeycloak,(function(e){return function(t){var a=t.keycloakInitialized,n=t.keycloak,c=Object(ie.a)(t,["keycloakInitialized","keycloak"]);return r.a.createElement(e,Object.assign({},c,{initialized:a,auth:n,authenticated:n.authenticated}))}})),ue=(a(69),J.a.prefix),se=function(e){var t=e.applicationBaseURL,a=(e.sessionUser,e.userDisplayName),n=e.currentPage,c=e.auth,i=e.authenticated,o=a||Object(x.get)(c,"idTokenParsed.preferred_username");return r.a.createElement("div",{className:"LoginButtonWidget"},i?r.a.createElement("div",{className:"LoginButtonWidget__wellcome"},r.a.createElement(M.a,{"aria-label":"My Account"},r.a.createElement(ce,{"aria-label":o,menuLinkName:o,renderMenuContent:function(){return r.a.createElement(R.a,{className:"".concat(ue,"--header__menu-arrow")})},className:"navigationMenu"},r.a.createElement(W.a,{href:"".concat(t,"do/main")},"Administration"),r.a.createElement(W.a,{onClick:function(){return c.logout({redirectUri:n})}},r.a.createElement(q.b,{className:"LoginButtonWidget__logoutsvg"})," Logout")))):r.a.createElement(q.a,{onClick:function(){return c.login({redirectUri:n})},className:"LoginButtonWidget__loginsvg"}))};se.defaultProps={sessionUser:"",userDisplayName:"",currentPage:"",authenticated:!1};var de=le(se),he=function(e){Object(s.a)(a,e);var t=Object(d.a)(a);function a(e){var n;return Object(o.a)(this,a),(n=t.call(this,e)).state={keycloak:null},n.kcOnEvent=n.kcOnEvent.bind(Object(u.a)(n)),n}return Object(l.a)(a,[{key:"componentDidMount",value:function(){var e=this.props.applicationBaseURL,t=new K.a("".concat(e,"keycloak.json"));this.setState({keycloak:t})}},{key:"setLocalKeys",value:function(e,t){localStorage.setItem("username",e),localStorage.setItem("token",t)}},{key:"unsetLocalKeys",value:function(){localStorage.removeItem("username"),localStorage.removeItem("token")}},{key:"kcOnEvent",value:function(e){var t=this.state.keycloak,a=Object(x.get)(t,"idTokenParsed.preferred_username"),n=t.token;switch(e){case"onAuthSuccess":case"onAuthRefreshSuccess":this.setLocalKeys(a,n);break;case"onAuthRefreshError":this.unsetLocalKeys(),t.logout();break;case"onAuthLogout":this.unsetLocalKeys()}}},{key:"render",value:function(){var e=this.state.keycloak;return e?r.a.createElement(H.KeycloakProvider,{keycloak:e,initConfig:{onLoad:"check-sso",checkLoginIframe:!0},onEvent:this.kcOnEvent},r.a.createElement(de,this.props)):null}}]),a}(n.Component);he.defaultProps={applicationBaseURL:Object({NODE_ENV:"production",PUBLIC_URL:"",WDS_SOCKET_HOST:void 0,WDS_SOCKET_PATH:void 0,WDS_SOCKET_PORT:void 0}).REACT_APP_BASEURL};var ge=he,pe={sessionUser:"session-user",userDisplayName:"user-display-name",currentPage:"page",applicationBaseURL:"app-url"},me=function(e){Object(s.a)(a,e);var t=Object(d.a)(a);function a(){return Object(o.a)(this,a),t.apply(this,arguments)}return Object(l.a)(a,[{key:"attributeChangedCallback",value:function(e,t,a){if(!Object.values(pe).includes(e))throw new Error("Untracked changed attribute: ".concat(e));this.mountPoint&&a!==t&&this.render()}},{key:"connectedCallback",value:function(){this.mountPoint=document.createElement("div"),this.appendChild(this.mountPoint),this.render()}},{key:"render",value:function(){var e=this.getAttribute(pe.sessionUser),t=this.getAttribute(pe.userDisplayName),a=this.getAttribute(pe.currentPage),n=this.getAttribute(pe.applicationBaseURL);i.a.render(r.a.createElement(ge,{sessionUser:e,userDisplayName:t,currentPage:a,applicationBaseURL:n}),this.mountPoint)}}],[{key:"observedAttributes",get:function(){return Object.values(pe)}}]),a}(Object(j.a)(HTMLElement));customElements.get("login-button-widget")||customElements.define("login-button-widget",me);a(70);var ve=J.a.prefix,fe=function(e){Object(s.a)(a,e);var t=Object(d.a)(a);function a(){var e;Object(o.a)(this,a);for(var n=arguments.length,r=new Array(n),c=0;c<n;c++)r[c]=arguments[c];return(e=t.call.apply(t,[this].concat(r))).currentPageMarked=!1,e}return Object(l.a)(a,[{key:"renderNav",value:function(e,t,a){var n=this,c=this.props.currentPage,i=0===Number(e[0].level),o=1===Number(e[0].level),l=i?M.a:ce,u=e.find((function(e){return e.code===c||e.children&&e.children.find((function(e){return e.code===c}))})),s="{}";if(t){t.children;var d=Object(ie.a)(t,["children"]);s=JSON.stringify(d)}var h,g=Object(te.a)(Object(te.a)(Object(te.a)({"aria-label":i?"Menu":t.title},i?{className:"navigationMenu"}:{menuLinkName:t.title,renderMenuContent:(h=o,function(){var e=h?R.a:R.c;return r.a.createElement(e,{className:"".concat(ve,"--header__menu-arrow")})}),uniqueId:s,key:s}),a?{isCurrentPage:a}:{}),t&&t.url&&!t.voidPage?{href:t.url}:{href:"#"});return r.a.createElement(l,g,e.map((function(e){var t=!n.currentPageMarked&&u&&u.code===e.code;return t&&(n.currentPageMarked=!0),e.children?n.renderNav(e.children,e,t):r.a.createElement(W.a,{key:JSON.stringify(e),href:e.url,isCurrentPage:t},e.title)})))}},{key:"render",value:function(){var e=this.props.navItems;return this.currentPageMarked=!1,this.renderNav(e)}}]),a}(n.Component),be={navItems:"nav-items",currentPage:"current-page"},Ee=function(e){Object(s.a)(a,e);var t=Object(d.a)(a);function a(){return Object(o.a)(this,a),t.apply(this,arguments)}return Object(l.a)(a,[{key:"attributeChangedCallback",value:function(e,t,a){if(!Object.values(be).includes(e))throw new Error("Untracked changed attribute: ".concat(e));this.mountPoint&&a!==t&&this.render()}},{key:"connectedCallback",value:function(){this.render()}},{key:"condenseNavItems",value:function(e){var t=JSON.parse(e).map((function(e,t){return Object(te.a)(Object(te.a)({},e),{},{navIdx:t})})),a=[],n=[];return t.forEach((function(e,r){if(Number(e.level)!==a.length)for(;Number(e.level)!==a.length;)e.level>a.length?a.push(t[r-1]):a.pop();var c=a.length;c>0?(a[c-1].children||(a[c-1].children=[]),a[c-1].children=[].concat(Object(T.a)(a[c-1].children),[e])):n.push(e)})),n}},{key:"render",value:function(){var e=this.condenseNavItems(this.getAttribute(be.navItems)),t=this.getAttribute(be.currentPage);i.a.render(r.a.createElement(fe,{navItems:e,currentPage:t}),this)}}],[{key:"observedAttributes",get:function(){return Object.values(be)}}]),a}(Object(j.a)(HTMLElement));customElements.get("navigation-bar-widget")||customElements.define("navigation-bar-widget",Ee);var Oe=a(34),_e=a(88),ke=(a(71),function(e){var t=e.actionUrl,a=e.placeholder,c=Object(n.useState)(!1),i=Object(Oe.a)(c,2),o=i[0],l=i[1],u=F()("SearchBar",o?"opened":"");return r.a.createElement("form",{action:t,className:u},o?r.a.createElement(r.a.Fragment,null,r.a.createElement("input",{type:"text",name:"search",placeholder:a}),r.a.createElement("button",{type:"submit"},r.a.createElement(_e.a,null))):r.a.createElement(_e.a,{className:"SearchBar__svg",onClick:function(){return l(!0)}}))}),ye={actionUrl:"action-url",placeholder:"placeholder"},Se=function(e){Object(s.a)(a,e);var t=Object(d.a)(a);function a(){return Object(o.a)(this,a),t.apply(this,arguments)}return Object(l.a)(a,[{key:"attributeChangedCallback",value:function(e,t,a){if(!Object.values(ye).includes(e))throw new Error("Untracked changed attribute: ".concat(e));a!==t&&this.render()}},{key:"connectedCallback",value:function(){this.render()}},{key:"render",value:function(){var e=this.getAttribute(ye.actionUrl),t=this.getAttribute(ye.placeholder);i.a.render(r.a.createElement(ke,{actionUrl:e,placeholder:t}),this)}}],[{key:"observedAttributes",get:function(){return Object.values(ye)}}]),a}(Object(j.a)(HTMLElement));customElements.get("search-bar-widget")||customElements.define("search-bar-widget",Se);a(72);var Ce=function(e){Object(s.a)(a,e);var t=Object(d.a)(a);function a(){var e;Object(o.a)(this,a);for(var n=arguments.length,r=new Array(n),c=0;c<n;c++)r[c]=arguments[c];return(e=t.call.apply(t,[this].concat(r))).handleLogoAreaRef=function(t){var a=e.props.childNodes.filter((function(e){return"logo"===e.getAttribute("role")})),n=Object(Oe.a)(a,1)[0];if(n){var r=n.cloneNode(!0);r.classList.add("HeaderFragment__logo"),t.appendChild(r)}},e.handleMenuAreaRef=function(t){var a=e.props.childNodes.filter((function(e){return"navigation-bar-widget"===e.tagName.toLowerCase()})),n=Object(Oe.a)(a,1)[0];n&&t.appendChild(n.cloneNode(!0))},e.handleActionsRef=function(t){e.props.childNodes.filter((function(e){var t=e.tagName.toLowerCase();return"navigation-bar-widget"!==t&&"script"!==t&&"link"!==t&&"logo"!==e.getAttribute("role")||"login-button-widget"===t||"choose-language-widget"===t})).forEach((function(e){var a=e.cloneNode(!0);a.classList.contains("navbar-search")&&a.classList.add("Homepage__search"),t.appendChild(a)}))},e}return Object(l.a)(a,[{key:"render",value:function(){this.props.applicationBaseURL;return r.a.createElement(m.a,{"aria-label":"Entando",className:"HeaderFragment"},r.a.createElement("a",{ref:this.handleLogoAreaRef}),r.a.createElement("div",{ref:this.handleMenuAreaRef,className:"HeaderFragment__menu-area"}),r.a.createElement("div",{ref:this.handleActionsRef,className:"bx--header__global HeaderFragment__actions-area"}))}}]),a}(n.Component);Ce.defaultProps={applicationBaseURL:Object({NODE_ENV:"production",PUBLIC_URL:"",WDS_SOCKET_HOST:void 0,WDS_SOCKET_PATH:void 0,WDS_SOCKET_PORT:void 0}).REACT_APP_BASEURL};var Pe=Ce,je={applicationBaseURL:"app-url"},Te=function(e){Object(s.a)(a,e);var t=Object(d.a)(a);function a(){var e;Object(o.a)(this,a);for(var n=arguments.length,r=new Array(n),c=0;c<n;c++)r[c]=arguments[c];return(e=t.call.apply(t,[this].concat(r))).observer=null,e.childs=[],e}return Object(l.a)(a,[{key:"attributeChangedCallback",value:function(e,t,a){if(!Object.values(je).includes(e))throw new Error("Untracked changed attribute: ".concat(e));a!==t&&this.childs.length&&this.extractTemplateTag()}},{key:"extractTemplateTag",value:function(e){var t=e||this.getElementsByTagName("template")[0];if(t){var a=t.content&&t.content.children;a.length?this.childs=Object(T.a)(a):this.childs=Object(T.a)(t.childNodes),this.render()}}},{key:"activateObserve",value:function(){var e=this,t=this.querySelector("template");t?this.extractTemplateTag(t):(this.observer=new MutationObserver((function(t){t.forEach((function(t){t.addedNodes.length,t.addedNodes.length&&t.addedNodes[0].tagName&&"template"===t.addedNodes[0].tagName.toLowerCase()&&setTimeout((function(){return e.extractTemplateTag(t.addedNodes[0])}),500)}))})),this.observer.observe(this,{childList:!0}))}},{key:"connectedCallback",value:function(){this.activateObserve()}},{key:"render",value:function(){var e=this.getAttribute(je.applicationBaseURL);i.a.render(r.a.createElement(Pe,{applicationBaseURL:e,childNodes:this.childs}),this)}}],[{key:"observedAttributes",get:function(){return Object.values(je)}}]),a}(Object(j.a)(HTMLElement));customElements.get("header-fragment")||customElements.define("header-fragment",Te);"true"===Object({NODE_ENV:"production",PUBLIC_URL:"",WDS_SOCKET_HOST:void 0,WDS_SOCKET_PATH:void 0,WDS_SOCKET_PORT:void 0}).REACT_APP_LOCAL&&i.a.render(r.a.createElement(P,null),document.getElementById("root"))}},[[56,1,2]]]);
//# sourceMappingURL=main.b881fe48.chunk.js.map