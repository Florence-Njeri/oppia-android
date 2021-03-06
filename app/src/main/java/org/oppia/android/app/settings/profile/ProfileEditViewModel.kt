package org.oppia.android.app.settings.profile

import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import org.oppia.android.app.activity.ActivityScope
import org.oppia.android.app.model.Profile
import org.oppia.android.app.model.ProfileId
import org.oppia.android.app.viewmodel.ObservableViewModel
import org.oppia.android.domain.profile.ProfileManagementController
import org.oppia.android.util.data.AsyncResult
import org.oppia.android.util.data.DataProviders.Companion.toLiveData
import org.oppia.android.util.logging.ConsoleLogger
import javax.inject.Inject

// TODO(#1633): Fix ViewModel to not depend on View
/** The ViewModel for [ProfileEditActivity]. */
@ActivityScope
class ProfileEditViewModel @Inject constructor(
  private val activity: AppCompatActivity,
  private val logger: ConsoleLogger,
  private val profileManagementController: ProfileManagementController
) : ObservableViewModel() {
  private lateinit var profileId: ProfileId
  private lateinit var switch: Switch

  lateinit var profileName: String

  val profile: LiveData<Profile> by lazy {
    Transformations.map(
      profileManagementController.getProfile(profileId).toLiveData(),
      ::processGetProfileResult
    )
  }

  var isAdmin = false

  fun setProfileId(id: Int, switch: Switch) {
    profileId = ProfileId.newBuilder().setInternalId(id).build()
    this.switch = switch
  }

  private fun processGetProfileResult(profileResult: AsyncResult<Profile>): Profile {
    if (profileResult.isFailure()) {
      logger.e(
        "ProfileEditViewModel",
        "Failed to retrieve the profile with ID: ${profileId.internalId}",
        profileResult.getErrorOrNull()!!
      )
    }
    val profile = profileResult.getOrDefault(Profile.getDefaultInstance())
    switch.isChecked = profile.allowDownloadAccess
    activity.title = profile.name
    profileName = profile.name
    isAdmin = profile.isAdmin
    return profile
  }
}
