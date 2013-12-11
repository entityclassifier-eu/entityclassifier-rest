<!--
To change this template, choose Tools | Templates
and open the template in the editor.
-->
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <jsp:include page="head-tag.jsp">
            <jsp:param name="title" value="THD Application" />
            <jsp:param name="description" value="Unsupervised Targeted Hypernym Discovery tool" />
            <jsp:param name="keywords" value="algorithm, hypernym discovery, program, tool, api, web api" />
    </jsp:include>
    <script src="/thd/public/script/script.js" type="text/javascript"></script>
    <script>
        $(document).ready(function () {
            $(".tooltipclass").tipTip({delay: 50, fadeIn: 150, fadeOut: 200});
      });
        $(document).ready(function () {
            $(".tooltipclass2").tipTip2({delay: 50, fadeIn: 150, fadeOut: 200});
      });
  </script>
    <body>
        <jsp:include page="title.jsp" />
        <jsp:include page="navigation-basic.jsp" >
            <jsp:param name="active-link" value="application" />
        </jsp:include>
        <div id="main">
                    <h2 style="text-align: center; padding-bottom: 17px; margin-top: -5px;">Extraction, Disambiguation and Classification of Entities and Named Entities</h1>
                    <div>
                        <div id="textframe">
                            <textarea id="textareacontainer" rows="15">The Charles Bridge is a famous historic bridge that crosses the Vltava river in Prague, Czech Republic.</textarea>
                        </div>

                        <div id="app-control">
                            <form onsubmit="invokeAPI(this); return false;" method="get" id="parametersForm">                               
<!--                                <div style="padding-top: 10px; padding-left: 20px;">
                                    <div style="float:left; width: 180px; margin-top: 8px;">Num. of hypernyms:</div>
                                    <div style="float:left;">
                                        <input id="q" class="maxResults" placeholder="Type in the max. num. of results" type="number" min="1" max="3" step="1" value="1" required="">
                                    </div>
                                </div>-->
                                <div style="clear:both; padding-left: 20px; padding-top: 15px; ">
                                    <div style="float:left; width: 220px; margin-top: 2px;">Request timeout (in seconds):</div>
                                    <div style="float:left; ">
                                        <input id="q" class="requestTimeout" style="width: 80px;" placeholder="Type in the max. request timeout (in sec.)" type="number" min="1" max="500" step="1" value="60" required="">
                                    </div>
                                </div>
                                <div id="mycheckboxform" style="clear:both; float:left; padding-left: 20px; padding-top: 10px; ">
                                    <div>Language of the input text</div>
                                    <div style="float:left;"><input class="langchkbox" id="Check1" type="checkbox" value="en" onclick="selectOnlyThisLang(this.id)" checked>English</input></div>
                                    <div style="float:left; margin-left: 10px;"><input class="langchkbox" id="Check2" type="checkbox" value="de" onclick="selectOnlyThisLang(this.id)">German</input></div>
                                    <div style="float:left; margin-left: 10px;"><input class="langchkbox" id="Check3" type="checkbox" value="nl" onclick="selectOnlyThisLang(this.id)">Dutch</input></div>
                                </div>
                                <div id="mycheckboxform" style="clear:both; float:left; padding-left: 20px; padding-top: 10px; ">
                                    <div><span class='tooltipclass2' title="</br>Once entity is disambiguated, provenance defines </br> from which knowledge base the types assigned to</br>the entities come from." style="border-bottom:1px dashed;" >Provenance of types</span></div>
                                    <div style="float:left;"><input class="provenance" id="Check10" type="checkbox" value="thd" onclick="selectOnlyThis4(this.id)" checked><span style="border-bottom:1px dashed;"   class="tooltipclass2" title="</br>Types are extracted from the free text of </br>Wikipedia articles.</br></br>">THD</span></input></div>
                                    <div style="float:left; margin-left: 10px;"><input class="provenance" id="Check11" type="checkbox" value="dbpedia" onclick="selectOnlyThis4(this.id)" checked><span style="border-bottom:1px dashed;" class="tooltipclass2" title="</br><a href='http://dbpedia.org'>DBpedia</a> (v3.8), languages: selected + English</br></br>">DBpedia</span></input></div>
                                    <div style="float:left; margin-left: 10px;"><input class="provenance" id="Check12" type="checkbox" value="yago" onclick="selectOnlyThis4(this.id)" checked><span style="border-bottom:1px dashed; width: 200px;" class="tooltipclass2" title="</br><a href='http://www.mpi-inf.mpg.de/yago-naga/yago/'>YAGO2s semantic knowledge base</a> </br></br>">Yago</span></input></div>
                                </div>
                                <div id="mycheckboxform" style="clear:both; float:left; padding-left: 20px; padding-top: 10px; ">
                                    <div>Knowledge base</div>
                                    <div style="display: block;"><input class="knowledgebase" id="Check4" type="checkbox" value="linkedHypernymsDataset" onclick="selectOnlyThis2(this.id)" checked><span style="border-bottom:1px dashed;" class='tooltipclass2' title="</br>THD types come from the <a href='http://ner.vse.cz/datasets/linkedhypernyms'>Linked Hypernyms Dataset</a></br></br>">Linked Hypernyms Dataset</span></input></div>
                                    <div style="display: block;"><input class="knowledgebase" id="Check5" type="checkbox" value="local" onclick="selectOnlyThis2(this.id)"><span class="tooltipclass2" title="</br>THD types are extracted at query time from our</br>Wikipedia mirror(the date of the snapshot in the</br>page footer)</br></br>" style="border-bottom:1px dashed;">Local Wikipedia mirror</span></input></div>
                                    <div style="display: block;"><input class="knowledgebase" id="Check6" type="checkbox" value="live" onclick="selectOnlyThis2(this.id)"><span class="tooltipclass2" title="</br>THD types are extracted at query time from *.wikipedia.org API.</br>Suitable for retrieving types for new, topical entities. You can</br>verify the difference by inserting entity names from the</br><a href='http://en.wikipedia.org/wiki/Wikipedia:New_articles_by_topic'>list of new Wikipedia articles</a>.</br>Note: slowest, not suitable for longer input text.</br></br>" style="border-bottom:1px dashed;">Live Wikipedia</span></input></div>
                                </div>
                                <div id="mycheckboxform" style="clear:both; float:left; padding-left: 20px; padding-top: 10px;">
                                    <div>Types of entities to extract</div>
                                    <div style="float:left;"><input class="entitytype" id="Check7" type="checkbox" value="ne" onclick="selectOnlyThis3(this.id)" checked>Named Entities</input></div>
                                    <div style="float:left; margin-left: 10px;"><input class="entitytype" id="Check8" type="checkbox" value="ce" onclick="selectOnlyThis3(this.id)">Common Entities</input></div>
                                    <div style="float:left; margin-left: 10px;"><input class="entitytype" id="Check9" type="checkbox" value="all" onclick="selectOnlyThis3(this.id)">All</input></div>
                                </div>
                                <div id="mycheckboxform" style="clear:both; float:left; padding-left: 20px; padding-top: 10px;">
                                    <div><span class='tooltipclass2' title="</br>If checked, the system will prefer linking more </br>precise DBpedia desambiguation (longer entity name). </br> This option may result to less entities being assigned</br>types." style="border-bottom:1px dashed;" >Force long entity linking</span></div>
                                    <div style="float:left;"><input id="longEntityLinking" type="checkbox"> on </input></div>
                                </div>
                                <button type="submit" class="ga-button" form="parametersForm" style="margin-top: 35px;"> Run! </button>
                            </form>
                        </div>
                    </div>
                    <div id="results-container">
                        <h3>Results</h3>
                        <img id="loading-gif"src="/thd/public/img/loading.gif" style="display: none;" />
                        <div class="textResults">
                            <div class="finalResults">
                                No results yet.
                            </div>
                            <div class="status"></div>     
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <jsp:include page="footer.jsp" />
        <div id="infobox">
            <center><h3 style="margin-left: 15px; margin-right: 45px; margin-top: 15px;">Detailed results</h3></center>
            <div id="showmore_results" style="margin-left: 15px; margin-right: 15px; margin-top: 15px;">
            </div>
            <div id="showmore_close" />
        </div>
    </body>
</html>
