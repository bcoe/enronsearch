// Hit's Java API and performs searches 
// on Enron dataset.
function EnronSearch(opts) {
  _.extend(this, {
    searchInput: null, // The search input field.
    searchResults: null, // The search results div.
    highlights: null, // The suggestion area.
    terms: null, // The terms currently being search for.
    searchTerms: [], // search terms.
    nonce: 0, // used to prevent multiple searches.
  }, opts);

  this.typeAheadSearch();
  this.clearSearch();
}

// Clear current search.
EnronSearch.prototype.clearSearch = function() {
  var _this = this;

  $('.delete-tag').live('click', function() {
    var parent = $(this).parents('span'),
      tag = parent.find('.value').text();

    _this.searchTerms = _(_this.searchTerms).filter(function(v) {
      return v !== tag;
    });

    _this.showTerms();
    _this.search();

    return false;
  });
}

// Wire up the type-ahead search.
EnronSearch.prototype.typeAheadSearch = function() {
  var _this = this,
    data = null;

  $('.button').live('click', function() {
    _this.completeTag(true);
    return false;
  });

  this.searchInput.keydown(function(e) {
    if ( e.keyCode === 9 && _this.searchInput.val().length) {
      _this.completeTag(true);
      return false;
    } else if (e.keyCode === 13) {
      _this.completeTag(false);
      return false;
    }

    _this.search();
  });
};

EnronSearch.prototype.completeTag = function(useSuggestion) {
  // Keep a canonical list of terms.
  if (this.highlights.hasClass('hidden') || !useSuggestion) {
    this.searchTerms.push(this.searchInput.val());
  } else {
    this.searchTerms.push(this.highlights.text());
  }

  // Reset input box.
  this.highlights.addClass('hidden');
  this.searchInput.val(''); // reset search field.

  this.showTerms();
  this.search();
};

// Displa term elements below search box.
EnronSearch.prototype.showTerms = function() {
  // Display tag elements.
  this.terms.html( _(this.searchTerms).map(function(v) {
    return '<span class="tag"><span class="value">' + v + '</span><a class="delete-tag" href="#">[x]</a></span>';
  }).join(', ') );
};

// Hit our Java Controller for Search results,
// throttled to once every 250ms.
EnronSearch.prototype.search = function() {
  this.nonce = this.nonce + 1;
  this.safeSearch( this.nonce );
};

EnronSearch.prototype.safeSearch = function(nonce) {
  var _this = this;

  // only search once every 250ms.
  setTimeout(function() {

    if (nonce !== _this.nonce) return;

    // Build query from previously entered searches
    // and from current value of search field.
    var terms = [].concat(_this.searchTerms);
    if (_this.searchInput.val()) terms.push( _this.searchInput.val() + '*');
    var query = terms.join(' AND ');

    // Clear search results if query is blank.
    if (!query) {
      _this.displaySearchResults( {hits: {hits: [], total: 0}} );
      _this.highlights.html('');
      return;
    }

    data = {q: query};

    $.ajax({
      method: 'get',
      url: '/search',
      data: data,
      success: function(results) {
        if (nonce == _this.nonce) {
          _this.displaySearchResults(results);
          _this.displayHighlighted(results);
        }
      }
    });

  }, 250);
};

// Display search results in UI.
EnronSearch.prototype.displaySearchResults = function(results) {
  var _this = this;

  this.searchResults.html('<span class="count">' + results.hits.total + ' search results</span>');

  results.hits.hits.forEach(function(hit) {
    var message = hit._source,
      element = $('<div class="search-results">\
        <b class="subject"></b>\
        <div><b>from: </b><i class="from"></i></div>\
        <div><b>to: </b><i class="to"></i></div>\
        <p class="body"></p>\
        <hr />\
      </div>');

    element.find('.subject').text(message.subject);
    element.find('.to').text(message.to);
    element.find('.from').text(message.from);
    element.find('.body').text(message.body.replace(/[\r\n]/, ' ').substring(0, 1024) + '…');

    _this.searchResults.append(element);
  });
};

// Suggest emails that we might filter by, based on partial
// matches in to and from field.
EnronSearch.prototype.displayHighlighted = function(results) {

  var _this = this;

  this.highlights.addClass('hidden');

  if (this.searchInput.val().length && results.hits.hits.length && results.hits.hits[0].highlight) {

    var toHighlight = results.hits.hits[0].highlight.to,
      highlight = null,
      from = results.hits.hits[0]._source.from;

    if (toHighlight) {

      // use jQuery to tokenize terms.
      var candidates = $( '<div>' + toHighlight.shift() + '</div>').find('em');

      _(candidates).each(function(candidate) {
        var candidate = $(candidate).text();

        if (_this.searchTerms.indexOf(candidate) === -1) {
          highlight = candidate;
          return;
        }
      });
    }

    // Was it the from field being highlighted?
    if ( !highlight && from.indexOf(this.searchInput.val()) > -1 ) {
      highlight = from;
    }

    // Don't highlight an email we already scope search
    // results to.
    if (highlight) {
      this.highlights.text(highlight);
      this.highlights.removeClass('hidden');
    }
  }
};